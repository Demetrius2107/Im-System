package com.vela.im.service.message.domain.service;

import com.alibaba.fastjson.JSONObject;

import com.vela.im.service.message.application.dto.req.SendMessageReq;
import com.vela.im.service.message.application.dto.resp.SendMessageResp;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.application.utils.CallbackService;
import com.vela.im.service.application.utils.ConversationIdGenerate;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.ConversationTypeEnum;
import com.vela.im.shared.types.enums.command.MessageCommand;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.shared.types.message.MessageContent;
import com.vela.im.shared.types.message.OfflineMessageContent;
import com.vela.im.codec.pack.message.ChatMessageAck;
import com.vela.im.codec.pack.message.MessageReciveServerAckPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * <p>Title: P2PMessageService</p>
 * <p>Description: 单聊消息处理服务，负责消息发送、ACK确认、多端同步、重试机制等核心逻辑。</p>
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

    /**
     * 处理单聊消息主流程
     * <p>消息去重校验 → 前置回调 → 生成 sequence → 异步存储/推送/ACK。</p>
     *
     * @param messageContent 消息内容
     */
    public void process(MessageContent messageContent){

        logger.info("开始处理消息: {}", messageContent.getMessageId());
        String fromId = messageContent.getFromId();
        String toId = messageContent.getToId();
        Integer appId = messageContent.getAppId();

        // Idempotent check: skip if already cached (duplicate message)
        MessageContent messageFromMessageIdCache = messageStoreService.getMessageFromMessageIdCache
                (messageContent.getAppId(), messageContent.getMessageId(), MessageContent.class);
        if (messageFromMessageIdCache != null){
            threadPoolExecutor.execute(() ->{
                ack(messageContent, ResponseVO.successResponse());
                // Sync to sender's other online devices
                syncToSender(messageFromMessageIdCache, messageFromMessageIdCache);
                // Dispatch to receiver's online devices
                List<ClientInfo> clientInfos = dispatchMessage(messageFromMessageIdCache);
                if(clientInfos.isEmpty()){
                    // Receiver is offline, send server-side ACK to sender
                    reciverAck(messageFromMessageIdCache);
                }
            });
            return;
        }

        // Pre-send callback (if configured)
        ResponseVO responseVO = ResponseVO.successResponse();
        if(appConfig.isSendMessageAfterCallback()){
            responseVO = callbackService.beforeCallback(messageContent.getAppId(), Constants.CallbackCommand.SendMessageBefore
                    , JSONObject.toJSONString(messageContent));
        }

        if(!responseVO.isOk()){
            ack(messageContent,responseVO);
            return;
        }

        // Generate monotonic sequence for this conversation
        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":"
                + Constants.SeqConstants.Message+ ":" + ConversationIdGenerate.generateP2PId(
                messageContent.getFromId(),messageContent.getToId()
        ));
        messageContent.setMessageSequence(seq);

        // Async processing: store → offline → ACK → sync → dispatch
            threadPoolExecutor.execute(() ->{
                // Persist message body & history via MQ (with fallback)
                messageStoreService.storeP2PMessage(messageContent);

                // Store offline message for receiver
                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyProperties(messageContent,offlineMessageContent);
                offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
                messageStoreService.storeOfflineMessage(offlineMessageContent);

                // 1. Send ACK to sender
                ack(messageContent,ResponseVO.successResponse());
                // 2. Sync to sender's other devices
                syncToSender(messageContent,messageContent);
                // 3. Dispatch to receiver's online devices
                List<ClientInfo> clientInfos = dispatchMessage(messageContent);

                // Cache message for idempotent check
                messageStoreService.setMessageFromMessageIdCache(messageContent.getAppId(),
                        messageContent.getMessageId(),messageContent);
                if(clientInfos.isEmpty()){
                    // Receiver offline, send server-side ACK
                    reciverAck(messageContent);
                }

                // Post-send callback (if configured)
                if(appConfig.isSendMessageAfterCallback()){
                    callbackService.callback(messageContent.getAppId(),Constants.CallbackCommand.SendMessageAfter,
                            JSONObject.toJSONString(messageContent));
                }

                logger.info("消息处理完成: {}", messageContent.getMessageId());
            });
    }

    /**
     * Generic retry with exponential backoff.
     *
     * @param operation     the operation to retry
     * @param operationName name for logging
     * @param maxRetries    max retry attempts
     * @param baseDelayMs   initial delay in ms (doubles each attempt)
     * @param <T>           return type
     * @return operation result
     */
    private <T> T retryWithBackoff(Supplier<T> operation, String operationName, int maxRetries, long baseDelayMs) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return operation.get();
            } catch (Exception e) {
                logger.warn("{} 失败 (第{}/{}次): {}", operationName, i + 1, maxRetries, e.getMessage());
                if (i == maxRetries - 1) {
                    logger.error("{} 重试耗尽，已失败 {} 次", operationName, maxRetries);
                    throw e;
                }
                try {
                    long delay = baseDelayMs * (1L << i); // 指数退避: 100ms, 200ms, 400ms, 800ms...
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(operationName + " 被中断", ie);
                }
            }
        }
        throw new RuntimeException(operationName + " 重试失败");
    }

    /**
     * Dispatch message to receiver with retry (max 3 attempts, exponential backoff).
     *
     * @param messageContent message to dispatch
     * @return list of online clients that received the message
     */
    private List<ClientInfo> dispatchMessage(MessageContent messageContent){
        try {
            return retryWithBackoff(() -> {
                List<ClientInfo> clientInfos = messageProducer.sendToUser(messageContent.getToId(),
                        MessageCommand.MSG_P2P, messageContent, messageContent.getAppId());
                if (clientInfos == null) {
                    throw new RuntimeException("dispatchMessage returned null");
                }
                return clientInfos;
            }, "dispatchMessage-" + messageContent.getMessageId(), 3, 100L);
        } catch (Exception e) {
            logger.error("Failed to dispatch message, msgId={}, toId={}, error={}",
                    messageContent.getMessageId(), messageContent.getToId(), e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Send ACK to sender with retry (max 3 attempts).
     *
     * @param messageContent the acknowledged message
     * @param responseVO     ACK result
     */
    private void ack(MessageContent messageContent, ResponseVO responseVO){
        logger.info("msg ack,msgId={},checkResult={}", messageContent.getMessageId(), responseVO.getCode());

        ChatMessageAck chatMessageAck = new
                ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence());
        responseVO.setData(chatMessageAck);
        try {
            retryWithBackoff(() -> {
                messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_ACK,
                        responseVO, messageContent);
                return true;
            }, "ack-" + messageContent.getMessageId(), 3, 100L);
        } catch (Exception e) {
            logger.error("Failed to send ACK, msgId={}, fromId={}, error={}",
                    messageContent.getMessageId(), messageContent.getFromId(), e.getMessage());
        }
    }

    /**
     * Send server-side receive confirmation to sender (max 2 retries).
     * Indicates that the server has stored the message even if the receiver is offline.
     *
     * @param messageContent the received message
     */
    public void reciverAck(MessageContent messageContent){
        MessageReciveServerAckPack pack = new MessageReciveServerAckPack();
        pack.setFromId(messageContent.getToId());
        pack.setToId(messageContent.getFromId());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setMessageSequence(messageContent.getMessageSequence());
        pack.setServerSend(true);
        try {
            retryWithBackoff(() -> {
                messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_RECIVE_ACK,
                        pack, new ClientInfo(messageContent.getAppId(), messageContent.getClientType(),
                                messageContent.getImei()));
                return true;
            }, "receiverAck-" + messageContent.getMessageId(), 2, 200L);
        } catch (Exception e) {
            logger.error("Failed to send receiver ACK, msgId={}, error={}",
                    messageContent.getMessageId(), e.getMessage());
        }
    }

    /**
     * Sync message to sender's other online devices (exclude current client).
     *
     * @param messageContent message to sync
     * @param clientInfo     current client info to exclude
     */
    private void syncToSender(MessageContent messageContent, ClientInfo clientInfo){
        try {
            retryWithBackoff(() -> {
                messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                        MessageCommand.MSG_P2P, messageContent, messageContent);
                return true;
            }, "syncToSender-" + messageContent.getMessageId(), 2, 100L);
        } catch (Exception e) {
            logger.error("Failed to sync to sender's other devices, msgId={}, error={}",
                    messageContent.getMessageId(), e.getMessage());
        }
    }

    /**
     * Check sender permissions: mute/ban status and friendship relation.
     *
     * @param fromId sender user ID
     * @param toId   receiver user ID
     * @param appId  application ID
     * @return check result
     */
    public ResponseVO imServerPermissionCheck(String fromId, String toId,
                                               Integer appId){
        ResponseVO responseVO = checkSendMessageService.checkSenderForvidAndMute(fromId, appId);
        if(!responseVO.isOk()){
            return responseVO;
        }
        responseVO = checkSendMessageService.checkFriendShip(fromId, toId, appId);
        return responseVO;
    }

    /**
     * Send a P2P message directly (synchronous path, used by REST API).
     *
     * @param req send message request
     * @return send message response with messageKey and timestamp
     */
    public SendMessageResp send(SendMessageReq req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        BeanUtils.copyProperties(req,message);
        // Persist message via MQ
        messageStoreService.storeP2PMessage(message);
        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());

        // Sync to sender's other devices
        syncToSender(message,message);
        // Dispatch to receiver
        dispatchMessage(message);
        return sendMessageResp;
    }
}
