package com.vela.im.service.group.domain.service;



import com.vela.im.service.group.application.dto.req.SendGroupMessageRequest;
import com.vela.im.service.message.application.dto.resp.SendMessageResp;
import com.vela.im.service.message.domain.service.MessageStoreService;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.command.GroupEventCommand;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.shared.types.message.GroupChatMessageContent;
import com.vela.im.shared.types.message.MessageContent;
import com.vela.im.shared.types.message.OfflineMessageContent;
import com.vela.im.codec.pack.message.ChatMessageAck;
import com.vela.im.shared.types.enums.MessageErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * <p>Title: GroupMessageService</p>
 * <p>Description: 群聊消息处理服务，负责群消息发送、成员分发、离线存储、ACK确认等。</p>
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
public class GroupMessageService {

    private static final Logger logger = LoggerFactory.getLogger(GroupMessageService.class);

    private final MessageProducer messageProducer;
    private final ImGroupMemberService imGroupMemberService;
    private final MessageStoreService messageStoreService;
    private final RedisSeq redisSeq;
    private final AppConfig appConfig;

    private final ThreadPoolExecutor threadPoolExecutor;

    /** Simple in-memory rate limiter: userId → timestamp of last message */
    private final ConcurrentHashMap<String, Long> rateLimiter = new ConcurrentHashMap<>();

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
                logger.warn("{} failed (attempt {}/{}): {}", operationName, i + 1, maxRetries, e.getMessage());
                if (i == maxRetries - 1) {
                    logger.error("{} retries exhausted after {} attempts", operationName, maxRetries);
                    throw e;
                }
                try {
                    long delay = baseDelayMs * (1L << i); // Exponential backoff: 100ms, 200ms, 400ms...
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(operationName + " interrupted", ie);
                }
            }
        }
        throw new RuntimeException(operationName + " retry failed");
    }

    public GroupMessageService(MessageProducer messageProducer,
                               ImGroupMemberService imGroupMemberService,
                               MessageStoreService messageStoreService,
                               RedisSeq redisSeq,
                               AppConfig appConfig) {
        this.messageProducer = messageProducer;
        this.imGroupMemberService = imGroupMemberService;
        this.messageStoreService = messageStoreService;
        this.redisSeq = redisSeq;
        this.appConfig = appConfig;
    }

    {
        final AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("message-group-thread-" + num.getAndIncrement());
            return thread;
        });
    }

    /**
     * Process group message: validate → rate limit → dedup → persist → notify members.
     *
     * @param messageContent the group message to process
     */
    public void process(GroupChatMessageContent messageContent){
        String fromId = messageContent.getFromId();

        // Boundary condition checks
        Result<Void> boundaryCheck = validateGroupMessage(messageContent);
        if (!boundaryCheck.isOk()) {
            ack(messageContent, boundaryCheck);
            logger.warn("Group message rejected by boundary check, msgId={}, reason={}",
                    messageContent.getMessageId(), boundaryCheck.getMsg());
            return;
        }

        // Rate limiting check per user
        Result<Void> rateCheck = checkRateLimit(fromId);
        if (!rateCheck.isOk()) {
            ack(messageContent, rateCheck);
            logger.warn("Group message rate limited, fromId={}, msgId={}", fromId, messageContent.getMessageId());
            return;
        }

        // Idempotent check: skip if already processed (duplicate message)
        GroupChatMessageContent messageFromMessageIdCache = messageStoreService.getMessageFromMessageIdCache(messageContent.getAppId(),
                messageContent.getMessageId(), GroupChatMessageContent.class);
        if(messageFromMessageIdCache != null){
            threadPoolExecutor.execute(() -> ackAndDispatch(messageContent));
            return;
        }
        // Generate monotonic sequence for this group conversation
        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.GroupMessage
                + messageContent.getGroupId());
        messageContent.setMessageSequence(seq);
        threadPoolExecutor.execute(() -> {
            // Persist message via MQ (with fallback)
            messageStoreService.storeGroupMessage(messageContent);

            // Fetch all group members for dispatch
            List<String> groupMemberId = imGroupMemberService.getGroupMemberId(messageContent.getGroupId(),
                    messageContent.getAppId());
            messageContent.setMemberId(groupMemberId);

            // Store offline message for each group member
            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            BeanUtils.copyProperties(messageContent, offlineMessageContent);
            offlineMessageContent.setToId(messageContent.getGroupId());
            messageStoreService.storeGroupOfflineMessage(offlineMessageContent, groupMemberId);

            ackAndDispatch(messageContent);

            // Cache message for idempotent check
            messageStoreService.setMessageFromMessageIdCache(messageContent.getAppId(),
                    messageContent.getMessageId(), messageContent);
        });
    }

    /**
     * Dispatch group message to all members with retry and failure tracking.
     */
    private void dispatchMessage(GroupChatMessageContent messageContent){
        if (messageContent.getMemberId() == null) {
            logger.warn("dispatchMessage skipped: memberId is null, groupId={}", messageContent.getGroupId());
            return;
        }
        List<String> failedMembers = new ArrayList<>();
        for (String memberId : messageContent.getMemberId()) {
            if(!memberId.equals(messageContent.getFromId())){
                boolean success = false;
                for (int retry = 0; retry < 2; retry++) {
                    try {
                        messageProducer.sendToUser(memberId,
                                GroupEventCommand.MSG_GROUP,
                                messageContent, messageContent.getAppId());
                        success = true;
                        break;
                    } catch (Exception e) {
                        logger.warn("Group message dispatch failed, memberId={}, msgId={}, retry={}, error={}",
                                memberId, messageContent.getMessageId(), retry + 1, e.getMessage());
                        try {
                            Thread.sleep(100L * (1L << retry)); // Exponential backoff: 100ms, 200ms
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                if (!success) {
                    failedMembers.add(memberId);
                    logger.error("Group message dispatch exhausted, memberId={}, msgId={}",
                            memberId, messageContent.getMessageId());
                }
            }
        }
        if (!failedMembers.isEmpty()) {
            logger.warn("Group message partial dispatch failure, msgId={}, groupId={}, failed={}/{}",
                    messageContent.getMessageId(), messageContent.getGroupId(),
                    failedMembers.size(), messageContent.getMemberId().size() - 1);
        }
    }

    /**
     * Send ACK to sender with retry (max 3 attempts, exponential backoff).
     *
     * @param messageContent the acknowledged message
     * @param result         ACK result
     */
    private void ack(MessageContent messageContent, Result<?> result){
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        Result<ChatMessageAck> ackResult = result.isOk()
                ? Result.ok(chatMessageAck)
                : Result.fail(result.getCode(), result.getMsg());
        ackResult.setData(chatMessageAck);
        try {
            retryWithBackoff(() -> {
                messageProducer.sendToUser(messageContent.getFromId(),
                        GroupEventCommand.GROUP_MSG_ACK,
                        ackResult, messageContent);
                return true;
            }, "group-ack-" + messageContent.getMessageId(), 3, 100L);
        } catch (Exception e) {
            logger.error("Failed to send group ACK, msgId={}, fromId={}, error={}",
                    messageContent.getMessageId(), messageContent.getFromId(), e.getMessage());
        }
    }

    /**
     * Sync message to sender's other online devices with retry (max 2 attempts).
     */
    private void syncToSender(GroupChatMessageContent messageContent, ClientInfo clientInfo){
        try {
            retryWithBackoff(() -> {
                messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                        GroupEventCommand.MSG_GROUP, messageContent, clientInfo);
                return true;
            }, "group-sync-" + messageContent.getMessageId(), 2, 100L);
        } catch (Exception e) {
            logger.error("Failed to sync group message to sender, msgId={}, error={}",
                    messageContent.getMessageId(), e.getMessage());
        }
    }

    /**
     * Send ACK, sync to sender's other devices and dispatch to all group members.
     *
     * @param messageContent the group message to handle
     */
    private void ackAndDispatch(GroupChatMessageContent messageContent) {
        ack(messageContent, Result.ok());
        syncToSender(messageContent, messageContent);
        dispatchMessage(messageContent);
    }

    /**
     * Validate group message boundary conditions before processing.
     * <p>Checks: empty fields, body size, message time sanity.</p>
     *
     * @param messageContent the group message to validate
     * @return success if all checks pass, error code otherwise
     */
    private Result<Void> validateGroupMessage(GroupChatMessageContent messageContent) {
        // Check fromId is not empty
        if (StringUtils.isBlank(messageContent.getFromId())) {
            return Result.fail(MessageErrorCode.MESSAGE_FROMID_EMPTY);
        }
        // Check groupId is not empty
        if (StringUtils.isBlank(messageContent.getGroupId())) {
            return Result.fail(MessageErrorCode.MESSAGE_TOID_EMPTY);
        }
        // Check message body is not empty
        if (StringUtils.isBlank(messageContent.getMessageBody())) {
            return Result.fail(MessageErrorCode.MESSAGE_BODY_EMPTY);
        }
        // Check message body size
        int maxSize = appConfig != null && appConfig.getMessageMaxSize() != null
                ? appConfig.getMessageMaxSize() : 65536;
        if (messageContent.getMessageBody().getBytes().length > maxSize) {
            return Result.fail(MessageErrorCode.MESSAGE_BODY_TOO_LARGE);
        }
        // Check message time sanity
        if (messageContent.getMessageTime() != null) {
            long now = System.currentTimeMillis();
            long maxDeviation = appConfig != null && appConfig.getMessageTimeMaxDeviation() != null
                    ? appConfig.getMessageTimeMaxDeviation() : 300000L;
            if (Math.abs(now - messageContent.getMessageTime()) > maxDeviation) {
                return Result.fail(MessageErrorCode.MESSAGE_TIME_INVALID);
            }
        }
        return Result.ok();
    }

    /**
     * Simple rate limiting check per user.
     *
     * @param userId the sender user ID
     * @return success if within limit, error code if rate exceeded
     */
    private Result<Void> checkRateLimit(String userId) {
        int rateLimit = appConfig != null && appConfig.getMessageRateLimit() != null
                ? appConfig.getMessageRateLimit() : 20;
        long now = System.nanoTime();
        long window = 1_000_000_000L;
        long minInterval = window / rateLimit;

        Long lastMsg = rateLimiter.get(userId);
        if (lastMsg != null && (now - lastMsg) < minInterval) {
            logger.warn("Rate limit exceeded for user={}, interval={}ns < minInterval={}ns",
                    userId, now - lastMsg, minInterval);
            return Result.fail(MessageErrorCode.MESSAGE_RATE_LIMITED);
        }
        rateLimiter.put(userId, now);
        return Result.ok();
    }

    /**
     * Send a group message directly (synchronous path, used by REST API).
     *
     * @param req send group message request
     * @return send message response with messageKey and timestamp
     */
    public SendMessageResp send(SendGroupMessageRequest req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        GroupChatMessageContent message = new GroupChatMessageContent();
        BeanUtils.copyProperties(req,message);

        // Persist group message via MQ
        messageStoreService.storeGroupMessage(message);

        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        // Sync to sender's other devices
        syncToSender(message,message);
        // Dispatch to all group members
        dispatchMessage(message);

        return sendMessageResp;

    }
}
