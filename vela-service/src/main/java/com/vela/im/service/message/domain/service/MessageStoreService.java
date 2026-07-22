package com.vela.im.service.message.domain.service;

import com.alibaba.fastjson.JSONObject;
import com.vela.im.service.conversation.domain.service.ConversationService;
import com.vela.im.service.group.domain.entity.ImGroupMessageHistoryEntity;
import com.vela.im.service.group.infrastructure.persistence.mapper.ImGroupMessageHistoryMapper;
import com.vela.im.service.message.domain.entity.ImMessageBodyEntity;
import com.vela.im.service.message.domain.entity.ImMessageHistoryEntity;
import com.vela.im.service.message.infrastructure.persistence.mapper.ImMessageBodyMapper;
import com.vela.im.service.message.infrastructure.persistence.mapper.ImMessageHistoryMapper;
import com.vela.im.service.application.utils.SnowflakeIdWorker;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.trace.TraceIdContext;
import com.vela.im.shared.types.enums.ConversationTypeEnum;
import com.vela.im.shared.types.enums.DelFlagEnum;
import com.vela.im.shared.types.message.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: MessageStoreService</p>
 * <p>Description: 消息存储领域服务，负责消息的持久化存储、MQ 降级写入、离线消息 ZSet 管理及缓存操作。</p>
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
public class MessageStoreService {

    private static final Logger logger = LoggerFactory.getLogger(MessageStoreService.class);

    private final ImMessageHistoryMapper imMessageHistoryMapper;
    private final ImMessageBodyMapper imMessageBodyMapper;
    private final SnowflakeIdWorker snowflakeIdWorker;
    private final ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ConversationService conversationService;
    private final AppConfig appConfig;

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

    public MessageStoreService(ImMessageHistoryMapper imMessageHistoryMapper,
                               ImMessageBodyMapper imMessageBodyMapper,
                               SnowflakeIdWorker snowflakeIdWorker,
                               ImGroupMessageHistoryMapper imGroupMessageHistoryMapper,
                               RabbitTemplate rabbitTemplate,
                               StringRedisTemplate stringRedisTemplate,
                               ConversationService conversationService,
                               AppConfig appConfig) {
        this.imMessageHistoryMapper = imMessageHistoryMapper;
        this.imMessageBodyMapper = imMessageBodyMapper;
        this.snowflakeIdWorker = snowflakeIdWorker;
        this.imGroupMessageHistoryMapper = imGroupMessageHistoryMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.conversationService = conversationService;
        this.appConfig = appConfig;
    }

    /**
     * 存储单聊消息，通过 MQ 异步发送到消息存储服务
     * <p>MQ 不可用时自动降级为同步直接写入 DB。</p>
     *
     * @param messageContent 单聊消息内容
     */
    @Transactional
    public void storeP2PMessage(MessageContent messageContent){
        ImMessageBody imMessageBodyEntity = extractMessageBody(messageContent);
        DoStoreP2PMessageDto dto = new DoStoreP2PMessageDto();
        dto.setMessageContent(messageContent);
        dto.setMessageBody(imMessageBodyEntity);
        messageContent.setMessageKey(imMessageBodyEntity.getMessageKey());
        try {
            // Send to MQ for async persistence
            rabbitTemplate.convertAndSend(Constants.RabbitConstants.StoreP2PMessage, "",
                    JSONObject.toJSONString(dto), buildTracePostProcessor());
        } catch (Exception e) {
            logger.error("MQ send failed for P2P message, fallback to direct DB write, msgKey={}, error={}",
                    imMessageBodyEntity.getMessageKey(), e.getMessage());
            // Fallback: write directly to DB when MQ is unavailable
            storeP2PMessageDirectly(imMessageBodyEntity, messageContent);
        }
    }

    /**
     * Fallback: write P2P message directly to DB when MQ is unavailable.
     *
     * @param messageBody    message body
     * @param messageContent message content
     */
    @Transactional
    public void storeP2PMessageDirectly(ImMessageBody messageBody, MessageContent messageContent) {
        try {
            ImMessageBodyEntity bodyEntity = new ImMessageBodyEntity();
            BeanUtils.copyProperties(messageBody, bodyEntity);
            imMessageBodyMapper.insert(bodyEntity);
            List<ImMessageHistoryEntity> histories = extractToP2PMessageHistory(messageContent, bodyEntity);
            imMessageHistoryMapper.insertBatchSomeColumn(histories);
            logger.warn("P2P message persisted to DB (fallback), msgKey={}", messageBody.getMessageKey());
        } catch (Exception dbEx) {
            logger.error("P2P 消息降级写入 DB 也失败，msgKey={}, error={}",
                    messageBody.getMessageKey(), dbEx.getMessage());
        }
    }

    /**
     * 提取消息体，生成 messageKey（雪花算法）
     *
     * @param messageContent 消息内容
     * @return 消息体对象
     */
    public ImMessageBody extractMessageBody(MessageContent messageContent){
        ImMessageBody messageBody = new ImMessageBody();
        messageBody.setAppId(messageContent.getAppId());
        messageBody.setMessageKey(SnowflakeIdWorker.nextId());
        messageBody.setCreateTime(System.currentTimeMillis());
        messageBody.setSecurityKey("");
        messageBody.setExtra(messageContent.getExtra());
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        messageBody.setMessageTime(messageContent.getMessageTime());
        messageBody.setMessageBody(messageContent.getMessageBody());
        return messageBody;
    }

    /**
     * 提取单聊双方的消息历史记录
     * <p>生成发送方和接收方两条消息历史记录，用于后续查询。</p>
     *
     * @param messageContent      消息内容
     * @param imMessageBodyEntity 消息体实体
     * @return 消息历史记录列表（发送方 + 接收方）
     */
    public List<ImMessageHistoryEntity> extractToP2PMessageHistory(MessageContent messageContent,
                                                                   ImMessageBodyEntity imMessageBodyEntity){
        List<ImMessageHistoryEntity> list = new ArrayList<>();
        ImMessageHistoryEntity fromHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent,fromHistory);
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setMessageKey(imMessageBodyEntity.getMessageKey());
        fromHistory.setCreateTime(System.currentTimeMillis());

        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent,toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(imMessageBodyEntity.getMessageKey());
        toHistory.setCreateTime(System.currentTimeMillis());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }

    /**
     * 存储群聊消息，通过 MQ 异步发送到消息存储服务
     * <p>MQ 不可用时自动降级为同步直接写入 DB。</p>
     *
     * @param messageContent 群聊消息内容
     */
    @Transactional
    public void storeGroupMessage(GroupChatMessageContent messageContent){
        ImMessageBody imMessageBody = extractMessageBody(messageContent);
        DoStoreGroupMessageDto dto = new DoStoreGroupMessageDto();
        dto.setMessageBody(imMessageBody);
        dto.setGroupChatMessageContent(messageContent);
        messageContent.setMessageKey(imMessageBody.getMessageKey());
        try {
            rabbitTemplate.convertAndSend(Constants.RabbitConstants.StoreGroupMessage,
                    "",
                    JSONObject.toJSONString(dto), buildTracePostProcessor());
        } catch (Exception e) {
            logger.error("MQ 发送群聊消息存储任务失败，降级直接写入 DB，msgKey={}, error={}",
                    imMessageBody.getMessageKey(), e.getMessage());
            // Fallback: write directly to DB when MQ is unavailable
            storeGroupMessageDirectly(imMessageBody, messageContent);
        }
    }

    /**
     * Fallback: write group message directly to DB when MQ is unavailable.
     *
     * @param messageBody    message body
     * @param messageContent group message content
     */
    @Transactional
    public void storeGroupMessageDirectly(ImMessageBody messageBody, GroupChatMessageContent messageContent) {
        try {
            ImMessageBodyEntity bodyEntity = new ImMessageBodyEntity();
            BeanUtils.copyProperties(messageBody, bodyEntity);
            imMessageBodyMapper.insert(bodyEntity);
            ImGroupMessageHistoryEntity groupHistory = extractToGroupMessageHistory(messageContent, bodyEntity);
            imGroupMessageHistoryMapper.insert(groupHistory);
            logger.warn("Group message persisted to DB (fallback), msgKey={}", messageBody.getMessageKey());
        } catch (Exception dbEx) {
            logger.error("Group message fallback DB write also failed, msgKey={}, error={}",
                    messageBody.getMessageKey(), dbEx.getMessage());
        }
    }

    /**
     * 提取群聊消息历史记录
     *
     * @param messageContent      群聊消息内容
     * @param messageBodyEntity   消息体实体
     * @return 群聊消息历史记录
     */
    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent
                                                                     messageContent, ImMessageBodyEntity messageBodyEntity){
        ImGroupMessageHistoryEntity result = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent,result);
        result.setGroupId(messageContent.getGroupId());
        result.setMessageKey(messageBodyEntity.getMessageKey());
        result.setCreateTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 缓存消息内容到 Redis（防重），TTL 300 秒
     *
     * @param appId     应用ID
     * @param messageId 消息ID
     * @param messageContent 消息内容
     */
    public void setMessageFromMessageIdCache(Integer appId, String messageId, Object messageContent){
        //appid : cache : messageId
        String key =appId + ":" + Constants.RedisConstants.cacheMessage + ":" + messageId;
        stringRedisTemplate.opsForValue().set(key,JSONObject.toJSONString(messageContent),300, TimeUnit.SECONDS);
    }

    /**
     * 从缓存获取消息内容（防重校验）
     *
     * @param appId     应用ID
     * @param messageId 消息ID
     * @param clazz     返回类型
     * @param <T>       泛型类型
     * @return 消息内容，不存在返回 null
     */
    public <T> T getMessageFromMessageIdCache(Integer appId,
                                              String messageId, Class<T> clazz){
        //appid : cache : messageId
        String key = appId + ":" + Constants.RedisConstants.cacheMessage + ":" + messageId;
        String msg = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(msg)){
            return null;
        }
        return JSONObject.parseObject(msg, clazz);
    }

    /**
     * 存储单人离线消息，ZSet 超限时降级写入 DB
     *
     * @param offlineMessage 离线消息内容
     */
    public void storeOfflineMessage(OfflineMessageContent offlineMessage){

        // Build Redis keys for sender and receiver offline queues
        String fromKey = offlineMessage.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMessage.getFromId();
        String toKey = offlineMessage.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMessage.getToId();

        ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
        try {
            // Evict oldest if sender queue exceeds limit, persist to DB as fallback
            evictIfExceeded(operations, fromKey);
            offlineMessage.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.P2P.getCode(),offlineMessage.getFromId(),offlineMessage.getToId()
            ));
            // Insert into sender queue, scored by messageKey
            operations.add(fromKey,JSONObject.toJSONString(offlineMessage),
                    offlineMessage.getMessageKey());

            // Evict oldest if receiver queue exceeds limit
            evictIfExceeded(operations, toKey);

            offlineMessage.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.P2P.getCode(),offlineMessage.getToId(),offlineMessage.getFromId()
            ));
            // Insert into receiver queue
            operations.add(toKey,JSONObject.toJSONString(offlineMessage),
                    offlineMessage.getMessageKey());
        } catch (Exception e) {
            logger.error("Redis offline message store failed, falling back to DB, fromId={}, toId={}, msgKey={}, error={}",
                    offlineMessage.getFromId(), offlineMessage.getToId(),
                    offlineMessage.getMessageKey(), e.getMessage());
            // Fallback: persist directly to DB when Redis is unavailable
            persistToMessageHistory(offlineMessage, offlineMessage.getFromId());
            persistToMessageHistory(offlineMessage, offlineMessage.getToId());
        }
    }

    /**
     * Evict the oldest message from ZSet when capacity exceeded, persist to DB before removal.
     */
    private void evictIfExceeded(ZSetOperations<String, String> operations, String key) {
        Long size = operations.zCard(key);
        if (size != null && size > appConfig.getOfflineMessageCount()) {
            // Fetch the oldest entry (lowest score)
            Set<ZSetOperations.TypedTuple<String>> oldestSet = operations.rangeWithScores(key, 0, 0);
            if (oldestSet != null && !oldestSet.isEmpty()) {
                ZSetOperations.TypedTuple<String> oldest = oldestSet.iterator().next();
                String oldestValue = oldest.getValue();
                if (oldestValue != null) {
                    try {
                        OfflineMessageContent evictedMsg = JSONObject.parseObject(oldestValue, OfflineMessageContent.class);
                        // Persist to DB before removal
                        persistToMessageHistory(evictedMsg, extractOwnerIdFromKey(key));
                        logger.warn("Offline message ZSet full, evicted to DB, key={}, evictedMsgKey={}",
                                key, evictedMsg.getMessageKey());
                    } catch (Exception e) {
                        logger.warn("Failed to parse evicted offline message, key={}, error={}", key, e.getMessage());
                    }
                }
            }
            // Remove the oldest entry
            operations.removeRange(key, 0, 0);
        }
    }

    /**
     * Persist offline message to message history table as fallback.
     */
    private void persistToMessageHistory(OfflineMessageContent msg, String ownerId) {
        try {
            ImMessageHistoryEntity history = new ImMessageHistoryEntity();
            history.setAppId(msg.getAppId());
            history.setFromId(msg.getFromId());
            history.setToId(msg.getToId());
            history.setOwnerId(ownerId);
            history.setMessageKey(msg.getMessageKey());
            history.setSequence(msg.getMessageSequence());
            history.setMessageRandom(msg.getMessageRandom());
            history.setMessageTime(msg.getMessageTime());
            history.setCreateTime(System.currentTimeMillis());
            imMessageHistoryMapper.insert(history);
        } catch (Exception e) {
            logger.error("Failed to persist offline message to DB, msgKey={}, ownerId={}, error={}",
                    msg.getMessageKey(), ownerId, e.getMessage());
        }
    }

    /**
     * Extract ownerId from Redis key.
     * Key format: appId:offlineMessage:ownerId
     */
    private String extractOwnerIdFromKey(String key) {
        String[] parts = key.split(":");
        if (parts.length >= 3) {
            return parts[2];
        }
        return "unknown";
    }


    /**
     * 存储群离线消息，ZSet 超限时降级写入 DB
     *
     * @param offlineMessage 离线消息内容
     * @param memberIds      群成员ID列表
     */
    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessage, List<String> memberIds){

        ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
        offlineMessage.setConversationType(ConversationTypeEnum.GROUP.getCode());

        for (String memberId : memberIds) {
            // Build Redis key for each group member's offline queue
            String toKey = offlineMessage.getAppId() + ":" +
                    Constants.RedisConstants.OfflineMessage + ":" +
                    memberId;
            offlineMessage.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.GROUP.getCode(),memberId,offlineMessage.getToId()
            ));
            try {
                evictIfExceeded(operations, toKey);
                // Insert into member's queue, scored by messageKey
                operations.add(toKey,JSONObject.toJSONString(offlineMessage),
                        offlineMessage.getMessageKey());
            } catch (Exception e) {
                logger.error("Redis group offline message store failed, falling back to DB, memberId={}, groupId={}, error={}",
                        memberId, offlineMessage.getToId(), e.getMessage());
                persistToMessageHistory(offlineMessage, memberId);
            }
        }
    }

}
