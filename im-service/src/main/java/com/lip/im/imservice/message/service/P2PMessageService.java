package com.lip.im.imservice.message.service;

import com.alibaba.fastjson.JSONObject;

import com.lip.im.imservice.message.model.req.SendMessageReq;
import com.lip.im.imservice.message.model.resp.SendMessageResp;
import com.lip.im.imservice.seq.RedisSeq;
import com.lip.im.imservice.utils.CallbackService;
import com.lip.im.imservice.utils.ConversationIdGenerate;
import com.lip.im.imservice.utils.MessageProducer;
import com.lip.im.model.ResponseVO;
import com.lip.im.model.config.AppConfig;
import com.lip.im.model.constants.Constants;
import com.lip.im.model.enums.ConversationTypeEnum;
import com.lip.im.model.enums.command.MessageCommand;
import com.lip.im.model.model.ClientInfo;
import com.lip.im.model.model.message.MessageContent;
import com.lip.im.model.model.message.OfflineMessageContent;
import com.lip.pack.message.ChatMessageAck;
import com.lip.pack.message.MessageReciveServerAckPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Service
public class P2PMessageService {

    private static Logger logger = LoggerFactory.getLogger(P2PMessageService.class);

    @Autowired
    CheckSendMessageService checkSendMessageService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    MessageStoreService messageStoreService;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    AppConfig appConfig;

    @Autowired
    CallbackService callbackService;


    private final ThreadPoolExecutor threadPoolExecutor;

    {
        final AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("message-process-thread-" + num.getAndIncrement());
                return thread;
            }
        });
    }

    //离线
    //存储介质
    //1.mysql
    //2.redis
    //怎么存？
    //list


    //历史消息

    //发送方客户端时间
    //messageKey
    //redis 1 2 3
    public void process(MessageContent messageContent){

        logger.info("消息开始处理：{}",messageContent.getMessageId());
        String fromId = messageContent.getFromId();
        String toId = messageContent.getToId();
        Integer appId = messageContent.getAppId();

        MessageContent messageFromMessageIdCache = messageStoreService.getMessageFromMessageIdCache
                (messageContent.getAppId(), messageContent.getMessageId(),MessageContent.class);
        if (messageFromMessageIdCache != null){
            threadPoolExecutor.execute(() ->{
                ack(messageContent, ResponseVO.successResponse());
                //2.发消息给同步在线端
                syncToSender(messageFromMessageIdCache,messageFromMessageIdCache);
                //3.发消息给对方在线端
                List<ClientInfo> clientInfos = dispatchMessage(messageFromMessageIdCache);
                if(clientInfos.isEmpty()){
                    //发送接收确认给发送方，要带上是服务端发送的标识
                    reciverAck(messageFromMessageIdCache);
                }
            });
            return;
        }

        //回调
        ResponseVO responseVO = ResponseVO.successResponse();
        if(appConfig.isSendMessageAfterCallback()){
            responseVO = callbackService.beforeCallback(messageContent.getAppId(), Constants.CallbackCommand.SendMessageBefore
                    , JSONObject.toJSONString(messageContent));
        }

        if(!responseVO.isOk()){
            ack(messageContent,responseVO);
            return;
        }

        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":"
                + Constants.SeqConstants.Message+ ":" + ConversationIdGenerate.generateP2PId(
                messageContent.getFromId(),messageContent.getToId()
        ));
        messageContent.setMessageSequence(seq);

        //前置校验
        //这个用户是否被禁言 是否被禁用
        //发送方和接收方是否是好友
//        ResponseVO responseVO = imServerPermissionCheck(fromId, toId, appId);
//        if(responseVO.isOk()){
            threadPoolExecutor.execute(() ->{
                //appId + Seq + (from + to) groupId
                messageStoreService.storeP2PMessage(messageContent);

                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyProperties(messageContent,offlineMessageContent);
                offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
                messageStoreService.storeOfflineMessage(offlineMessageContent);

                //插入数据
                //1.回ack成功给自己
                ack(messageContent,ResponseVO.successResponse());
                //2.发消息给同步在线端
                syncToSender(messageContent,messageContent);
                //3.发消息给对方在线端
                List<ClientInfo> clientInfos = dispatchMessage(messageContent);

                messageStoreService.setMessageFromMessageIdCache(messageContent.getAppId(),
                        messageContent.getMessageId(),messageContent);
                if(clientInfos.isEmpty()){
                    //发送接收确认给发送方，要带上是服务端发送的标识
                    reciverAck(messageContent);
                }

                if(appConfig.isSendMessageAfterCallback()){
                    callbackService.callback(messageContent.getAppId(),Constants.CallbackCommand.SendMessageAfter,
                            JSONObject.toJSONString(messageContent));
                }

                logger.info("消息处理完成：{}",messageContent.getMessageId());
            });
//        }else{
//            //告诉客户端失败了
//            //ack
//            ack(messageContent,responseVO);
//        }
    }

    private List<ClientInfo> dispatchMessage(MessageContent messageContent){
        List<ClientInfo> clientInfos = messageProducer.sendToUser(messageContent.getToId(), MessageCommand.MSG_P2P,
                messageContent, messageContent.getAppId());
        return clientInfos;
    }

    private void ack(MessageContent messageContent,ResponseVO responseVO){
        logger.info("msg ack,msgId={},checkResut{}",messageContent.getMessageId(),responseVO.getCode());

        ChatMessageAck chatMessageAck = new
                ChatMessageAck(messageContent.getMessageId(),messageContent.getMessageSequence());
        responseVO.setData(chatMessageAck);
        //發消息
        messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_ACK,
                responseVO,messageContent
                );
    }

    public void reciverAck(MessageContent messageContent){
        MessageReciveServerAckPack pack = new MessageReciveServerAckPack();
        pack.setFromId(messageContent.getToId());
        pack.setToId(messageContent.getFromId());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setMessageSequence(messageContent.getMessageSequence());
        pack.setServerSend(true);
        messageProducer.sendToUser(messageContent.getFromId(),MessageCommand.MSG_RECIVE_ACK,
                pack,new ClientInfo(messageContent.getAppId(),messageContent.getClientType()
                ,messageContent.getImei()));
    }

    private void syncToSender(MessageContent messageContent, ClientInfo clientInfo){
            messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                    MessageCommand.MSG_P2P,messageContent,messageContent);
    }

    public ResponseVO imServerPermissionCheck(String fromId,String toId,
                                               Integer appId){
        ResponseVO responseVO = checkSendMessageService.checkSenderForvidAndMute(fromId, appId);
        if(!responseVO.isOk()){
            return responseVO;
        }
        responseVO = checkSendMessageService.checkFriendShip(fromId, toId, appId);
        return responseVO;
    }

    public SendMessageResp send(SendMessageReq req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        BeanUtils.copyProperties(req,message);
        //插入数据
        messageStoreService.storeP2PMessage(message);
        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());

        //2.发消息给同步在线端
        syncToSender(message,message);
        //3.发消息给对方在线端
        dispatchMessage(message);
        return sendMessageResp;
    }
}
