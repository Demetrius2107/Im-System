package com.vela.im.tcp.interfaces.publish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.command.CommandType;
import com.vela.im.codec.protocol.Message;
import com.vela.im.codec.protocol.MessageHeader;
import com.vela.im.tcp.infrastructure.utils.MqFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.vela.im.shared.trace.TraceIdContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>Title: MqMessageProducer</p>
 * <p>Description: MQ 消息生产者，网关层将消息投递到逻辑层对应的 RabbitMQ 队列</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-05
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
public class MqMessageProducer {

    /**
     * 发送 Message 消息到 MQ，根据 command 类型路由到对应的服务队列
     *
     * @param message 消息体
     * @param command 指令类型
     */
    public static void sendMessage(Message message ,Integer command){
        Channel channel = null;
        String com = command.toString();
        String commandSub = com.substring(0,1);
        CommandType commandType = CommandType.getCommandType(commandSub);
        String channelName = "";
        if(commandType == CommandType.MESSAGE){
            channelName = Constants.RabbitConstants.Im2MessageService;
        } else if (commandType == CommandType.GROUP){
            channelName = Constants.RabbitConstants.Im2GroupService;
        } else if(commandType == CommandType.FRIEND){
            channelName = Constants.RabbitConstants.Im2FriendshipService;
        } else if(commandType == CommandType.USER){
            channelName = Constants.RabbitConstants.Im2UserService;
        }

        try{
            channel = MqFactory.getChannel(channelName);

            JSONObject o = (JSONObject) JSON.toJSON(message.getMessagePack());
            o.put("command",command);
            o.put("clientType",message.getMessageHeader().getClientType());
            o.put("imei",message.getMessageHeader().getImei());
            o.put("appId",message.getMessageHeader().getAppId());
            channel.basicPublish(channelName,"",
                    buildTraceProperties(),o.toJSONString().getBytes());

        } catch (IOException e) {
            log.error("发送消息出现异常:{}",e.getMessage());
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log.error("发送消息出现异常:{}",e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /**
     * 发送消息体与消息头到 MQ，根据 command 类型路由到对应的服务队列
     *
     * @param message 消息体对象
     * @param header  消息头
     * @param command 指令类型
     */
    public static void sendMessage(Object message, MessageHeader header, Integer command){
        Channel channel = null;
        String com = command.toString();
        String commandSub = com.substring(0, 1);
        CommandType commandType = CommandType.getCommandType(commandSub);
        String channelName = "";
        if(commandType == CommandType.MESSAGE){
            channelName = Constants.RabbitConstants.Im2MessageService;
        }else if(commandType == CommandType.GROUP){
            channelName = Constants.RabbitConstants.Im2GroupService;
        }else if(commandType == CommandType.FRIEND){
            channelName = Constants.RabbitConstants.Im2FriendshipService;
        }else if(commandType == CommandType.USER){
            channelName = Constants.RabbitConstants.Im2UserService;
        }

        try {
            channel = MqFactory.getChannel(channelName);

            JSONObject o = (JSONObject) JSON.toJSON(message);
            o.put("command",command);
            o.put("clientType",header.getClientType());
            o.put("imei",header.getImei());
            o.put("appId",header.getAppId());
            channel.basicPublish(channelName,"",
                    buildTraceProperties(), o.toJSONString().getBytes());
        }catch (Exception e){
            log.error("发送消息出现异常：{}",e.getMessage());
        }
    }

    /**
     * 构建携带 TraceId 的 AMQP BasicProperties
     *
     * @return AMQP 消息属性
     */
    private static AMQP.BasicProperties buildTraceProperties() {
        Map<String, Object> headers = new HashMap<>(2);
        String traceId = TraceIdContext.get();
        if (traceId != null && !traceId.isEmpty()) {
            headers.put(Constants.TraceId.MQ_HEADER_NAME, traceId);
        }
        return new AMQP.BasicProperties.Builder()
                .headers(headers)
                .build();
    }

}