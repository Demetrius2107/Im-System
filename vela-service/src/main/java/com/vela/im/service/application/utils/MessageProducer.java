package com.vela.im.service.application.utils;

import com.alibaba.fastjson.JSONObject;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.trace.TraceIdContext;
import com.vela.im.shared.types.enums.command.Command;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.shared.types.UserSession;
import com.vela.im.codec.protocol.MessagePack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>Title: MessageProducer</p>
 * <p>Description: 消息生产者，通过 RabbitMQ 将消息推送到指定用户的 IM 网关节点。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-06
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class MessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final UserSessionUtils userSessionUtils;

    private final String queueName = Constants.RabbitConstants.MessageService2Im;

    public MessageProducer(RabbitTemplate rabbitTemplate,
                           UserSessionUtils userSessionUtils) {
        this.rabbitTemplate = rabbitTemplate;
        this.userSessionUtils = userSessionUtils;
    }

    public boolean sendMessage(UserSession session, Object msg){
        try {
            logger.info("send message == " + msg);
            rabbitTemplate.convertAndSend(queueName,session.getBrokerId()+"",msg, buildTracePostProcessor());
            return true;
        }catch (Exception e){
            logger.error("send error :" + e.getMessage());
            return false;
        }
    }

    /**
     * 构建 TraceId 透传的 MessagePostProcessor
     *
     * @return 消息后置处理器
     */
    private MessagePostProcessor buildTracePostProcessor() {
        return new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                String traceId = TraceIdContext.get();
                if (traceId != null && !traceId.isEmpty()) {
                    message.getMessageProperties().setHeader(Constants.TraceId.MQ_HEADER_NAME, traceId);
                }
                return message;
            }
        };
    }

    //包装数据，调用sendMessage
    public boolean sendPack(String toId, Command command, Object msg, UserSession session){
        MessagePack messagePack = new MessagePack();
        messagePack.setCommand(command.getCommand());
        messagePack.setToId(toId);
        messagePack.setClientType(session.getClientType());
        messagePack.setAppId(session.getAppId());
        messagePack.setImei(session.getImei());
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(msg));
        messagePack.setData(jsonObject);

        String body = JSONObject.toJSONString(messagePack);
        return sendMessage(session, body);
    }

    // 发送给所有端的方法
    public List<ClientInfo> sendToUser(String toId, Command command, Object data, Integer appId){
        List<UserSession> userSession
                = userSessionUtils.getUserSession(appId, toId);
        List<ClientInfo> list = new ArrayList<>();
        for (UserSession session : userSession) {
            boolean b = sendPack(toId, command, data, session);
            if(b){
                list.add(new ClientInfo(session.getAppId(),session.getClientType(),session.getImei()));
            }
        }
        return list;
    }

    public void sendToUser(String toId, Integer clientType,String imei, Command command,
                           Object data, Integer appId){
        if(clientType != null && StringUtils.isNotBlank(imei)){
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(toId,command,data,clientInfo);
        }else{
            sendToUser(toId,command,data,appId);
        }
    }

    //发送给某个用户的指定客户端
    public void sendToUser(String toId, Command command
            , Object data, ClientInfo clientInfo){
        UserSession userSession = userSessionUtils.getUserSession(clientInfo.getAppId(), toId, clientInfo.getClientType(),
                clientInfo.getImei());
        sendPack(toId,command,data,userSession);
    }

    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return Objects.equals(sessionDto.getAppId(), clientInfo.getAppId())
                && Objects.equals(sessionDto.getImei(), clientInfo.getImei())
                && Objects.equals(sessionDto.getClientType(), clientInfo.getClientType());
    }

    // 发送给除了某一端的其他端
    public void sendToUserExceptClient(String toId, Command command
            , Object data, ClientInfo clientInfo){
        List<UserSession> userSession = userSessionUtils
                .getUserSession(clientInfo.getAppId(),
                        toId);
        for (UserSession session : userSession) {
            if(!isMatch(session,clientInfo)){
                sendPack(toId,command,data,session);
            }
        }
    }

}
