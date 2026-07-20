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
import com.vela.im.shared.types.enums.ConversationTypeEnum;
import com.vela.im.shared.types.enums.DelFlagEnum;
import com.vela.im.shared.types.message.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Service
public class MessageStoreService {

    private static Logger logger = LoggerFactory.getLogger(MessageStoreService.class);

    @Autowired
    ImMessageHistoryMapper imMessageHistoryMapper;

    @Autowired
    ImMessageBodyMapper imMessageBodyMapper;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ConversationService conversationService;

    @Autowired
    AppConfig appConfig;

    @Transactional
    public void storeP2PMessage(MessageContent messageContent){
        ImMessageBody imMessageBodyEntity = extractMessageBody(messageContent);
        DoStoreP2PMessageDto dto = new DoStoreP2PMessageDto();
        dto.setMessageContent(messageContent);
        dto.setMessageBody(imMessageBodyEntity);
        messageContent.setMessageKey(imMessageBodyEntity.getMessageKey());
        try {
            rabbitTemplate.convertAndSend(Constants.RabbitConstants.StoreP2PMessage, "",
                    JSONObject.toJSONString(dto));
        } catch (Exception e) {
            logger.error("MQ 发送 P2P 消息存储任务失败，降级直接写入 DB，msgKey={}, error={}",
                    imMessageBodyEntity.getMessageKey(), e.getMessage());
            // MQ 不可用时，直接同步写入 DB 作为降级
            storeP2PMessageDirectly(imMessageBodyEntity, messageContent);
        }
    }

    /**
     * MQ 不可用时的降级方案：直接同步写入 DB
     */
    @Transactional
    public void storeP2PMessageDirectly(ImMessageBody messageBody, MessageContent messageContent) {
        try {
            ImMessageBodyEntity bodyEntity = new ImMessageBodyEntity();
            BeanUtils.copyProperties(messageBody, bodyEntity);
            imMessageBodyMapper.insert(bodyEntity);
            List<ImMessageHistoryEntity> histories = extractToP2PMessageHistory(messageContent, bodyEntity);
            imMessageHistoryMapper.insertBatchSomeColumn(histories);
            logger.warn("P2P 消息已降级写入 DB，msgKey={}", messageBody.getMessageKey());
        } catch (Exception dbEx) {
            logger.error("P2P 消息降级写入 DB 也失败，msgKey={}, error={}",
                    messageBody.getMessageKey(), dbEx.getMessage());
        }
    }

    public ImMessageBody extractMessageBody(MessageContent messageContent){
        ImMessageBody messageBody = new ImMessageBody();
        messageBody.setAppId(messageContent.getAppId());
        messageBody.setMessageKey(snowflakeIdWorker.nextId());
        messageBody.setCreateTime(System.currentTimeMillis());
        messageBody.setSecurityKey("");
        messageBody.setExtra(messageContent.getExtra());
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        messageBody.setMessageTime(messageContent.getMessageTime());
        messageBody.setMessageBody(messageContent.getMessageBody());
        return messageBody;
    }

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
                    JSONObject.toJSONString(dto));
        } catch (Exception e) {
            logger.error("MQ 发送群聊消息存储任务失败，降级直接写入 DB，msgKey={}, error={}",
                    imMessageBody.getMessageKey(), e.getMessage());
            // MQ 不可用时，直接同步写入 DB 作为降级
            storeGroupMessageDirectly(imMessageBody, messageContent);
        }
    }

    /**
     * MQ 不可用时的降级方案：直接同步写入群聊消息 DB
     */
    @Transactional
    public void storeGroupMessageDirectly(ImMessageBody messageBody, GroupChatMessageContent messageContent) {
        try {
            ImMessageBodyEntity bodyEntity = new ImMessageBodyEntity();
            BeanUtils.copyProperties(messageBody, bodyEntity);
            imMessageBodyMapper.insert(bodyEntity);
            ImGroupMessageHistoryEntity groupHistory = extractToGroupMessageHistory(messageContent, bodyEntity);
            imGroupMessageHistoryMapper.insert(groupHistory);
            logger.warn("群聊消息已降级写入 DB，msgKey={}", messageBody.getMessageKey());
        } catch (Exception dbEx) {
            logger.error("群聊消息降级写入 DB 也失败，msgKey={}, error={}",
                    messageBody.getMessageKey(), dbEx.getMessage());
        }
    }

    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent
                                                                     messageContent , ImMessageBodyEntity messageBodyEntity){
        ImGroupMessageHistoryEntity result = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent,result);
        result.setGroupId(messageContent.getGroupId());
        result.setMessageKey(messageBodyEntity.getMessageKey());
        result.setCreateTime(System.currentTimeMillis());
        return result;
    }

    public void setMessageFromMessageIdCache(Integer appId,String messageId,Object messageContent){
        //appid : cache : messageId
        String key =appId + ":" + Constants.RedisConstants.cacheMessage + ":" + messageId;
        stringRedisTemplate.opsForValue().set(key,JSONObject.toJSONString(messageContent),300, TimeUnit.SECONDS);
    }

    public <T> T getMessageFromMessageIdCache(Integer appId,
                                              String messageId,Class<T> clazz){
        //appid : cache : messageId
        String key = appId + ":" + Constants.RedisConstants.cacheMessage + ":" + messageId;
        String msg = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(msg)){
            return null;
        }
        return JSONObject.parseObject(msg, clazz);
    }

    /**
     * @description: 存储单人离线消息，ZSet 超限时降级写入 DB
     * @param
     * @return void
     * @author wanqiu 
     */
    public void storeOfflineMessage(OfflineMessageContent offlineMessage){

        // 找到fromId的队列
        String fromKey = offlineMessage.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMessage.getFromId();
        // 找到toId的队列
        String toKey = offlineMessage.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMessage.getToId();

        ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
        try {
            // 判断 from 队列是否超过设定值，超限时降级写入 DB
            evictIfExceeded(operations, fromKey, offlineMessage);
            offlineMessage.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.P2P.getCode(),offlineMessage.getFromId(),offlineMessage.getToId()
            ));
            // 插入数据 根据messageKey 作为分值
            operations.add(fromKey,JSONObject.toJSONString(offlineMessage),
                    offlineMessage.getMessageKey());

            // 判断 to 队列是否超过设定值，超限时降级写入 DB
            evictIfExceeded(operations, toKey, offlineMessage);

            offlineMessage.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.P2P.getCode(),offlineMessage.getToId(),offlineMessage.getFromId()
            ));
            // 插入数据 根据messageKey 作为分值
            operations.add(toKey,JSONObject.toJSONString(offlineMessage),
                    offlineMessage.getMessageKey());
        } catch (Exception e) {
            logger.error("Redis 存储离线消息失败，降级写入 DB，fromId={}, toId={}, msgKey={}, error={}",
                    offlineMessage.getFromId(), offlineMessage.getToId(),
                    offlineMessage.getMessageKey(), e.getMessage());
            // Redis 不可用时，直接写入 DB 作为降级
            persistToMessageHistory(offlineMessage, offlineMessage.getFromId());
            persistToMessageHistory(offlineMessage, offlineMessage.getToId());
        }
    }

    /**
     * ZSet 超限时：将最旧的一条消息持久化到 DB 后再移除
     */
    private void evictIfExceeded(ZSetOperations<String, String> operations, String key,
                                 OfflineMessageContent currentMessage) {
        Long size = operations.zCard(key);
        if (size != null && size > appConfig.getOfflineMessageCount()) {
            // 获取最旧的一条（score 最小）
            Set<ZSetOperations.TypedTuple<String>> oldestSet = operations.rangeWithScores(key, 0, 0);
            if (oldestSet != null && !oldestSet.isEmpty()) {
                ZSetOperations.TypedTuple<String> oldest = oldestSet.iterator().next();
                String oldestValue = oldest.getValue();
                if (oldestValue != null) {
                    try {
                        OfflineMessageContent evictedMsg = JSONObject.parseObject(oldestValue, OfflineMessageContent.class);
                        // 降级写入 DB
                        persistToMessageHistory(evictedMsg, extractOwnerIdFromKey(key));
                        logger.warn("离线消息 ZSet 超限，降级写入 DB，key={}, evictedMsgKey={}",
                                key, evictedMsg.getMessageKey());
                    } catch (Exception e) {
                        logger.warn("解析被驱逐的离线消息失败，key={}, error={}", key, e.getMessage());
                    }
                }
            }
            // 移除最旧的一条
            operations.removeRange(key, 0, 0);
        }
    }

    /**
     * 将离线消息持久化到消息历史表
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
            logger.error("离线消息持久化到 DB 失败，msgKey={}, ownerId={}, error={}",
                    msg.getMessageKey(), ownerId, e.getMessage());
        }
    }

    /**
     * 从 Redis key 中提取 ownerId
     * key 格式: appId:offlineMessage:ownerId
     */
    private String extractOwnerIdFromKey(String key) {
        String[] parts = key.split(":");
        if (parts.length >= 3) {
            return parts[2];
        }
        return "unknown";
    }


    /**
     * @description: 存储群离线消息，ZSet 超限时降级写入 DB
     * @param
     * @return void
     * @author wanqiu
     */
    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessage
    ,List<String> memberIds){

        ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
        offlineMessage.setConversationType(ConversationTypeEnum.GROUP.getCode());

        for (String memberId : memberIds) {
            // 找到toId的队列
            String toKey = offlineMessage.getAppId() + ":" +
                    Constants.RedisConstants.OfflineMessage + ":" +
                    memberId;
            offlineMessage.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.GROUP.getCode(),memberId,offlineMessage.getToId()
            ));
            try {
                evictIfExceeded(operations, toKey, offlineMessage);
                // 插入数据 根据messageKey 作为分值
                operations.add(toKey,JSONObject.toJSONString(offlineMessage),
                        offlineMessage.getMessageKey());
            } catch (Exception e) {
                logger.error("Redis 存储群离线消息失败，降级写入 DB，memberId={}, groupId={}, error={}",
                        memberId, offlineMessage.getToId(), e.getMessage());
                persistToMessageHistory(offlineMessage, memberId);
            }
        }
    }

}
