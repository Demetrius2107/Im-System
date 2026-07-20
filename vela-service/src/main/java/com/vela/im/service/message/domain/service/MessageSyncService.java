package com.vela.im.service.message.domain.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import com.vela.im.service.conversation.domain.service.ConversationService;
import com.vela.im.service.group.domain.service.ImGroupMemberService;
import com.vela.im.service.message.domain.entity.ImMessageBodyEntity;
import com.vela.im.service.message.infrastructure.persistence.mapper.ImMessageBodyMapper;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.application.utils.ConversationIdGenerate;
import com.vela.im.service.application.utils.GroupMessageProducer;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.service.application.utils.SnowflakeIdWorker;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.ConversationTypeEnum;
import com.vela.im.shared.types.enums.DelFlagEnum;
import com.vela.im.shared.types.enums.MessageErrorCode;
import com.vela.im.shared.types.enums.command.Command;
import com.vela.im.shared.types.enums.command.GroupEventCommand;
import com.vela.im.shared.types.enums.command.MessageCommand;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.shared.types.SyncReq;
import com.vela.im.shared.types.SyncResp;
import com.vela.im.shared.types.message.MessageReadedContent;
import com.vela.im.shared.types.message.MessageReceiveAckContent;
import com.vela.im.shared.types.message.OfflineMessageContent;
import com.vela.im.shared.types.message.RecallMessageContent;
import com.vela.im.codec.pack.message.MessageReadedPack;
import com.vela.im.codec.pack.message.RecallMessageNotifyPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: MessageSyncService</p>
 * <p>Description: 消息同步与已读回执服务，处理多端消息同步、已读回执、离线消息拉取、消息撤回等核心同步逻辑。</p>
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
public class MessageSyncService {

    private final MessageProducer messageProducer;
    private final ConversationService conversationService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ImMessageBodyMapper imMessageBodyMapper;
    private final RedisSeq redisSeq;
    private final ImGroupMemberService imGroupMemberService;
    private final GroupMessageProducer groupMessageProducer;
    private final AppConfig appConfig;

    private static final Logger logger = LoggerFactory.getLogger(MessageSyncService.class);

    /** Concurrent lock per messageKey to prevent concurrent recall operations */
    private final ConcurrentHashMap<Long, Object> recallLocks = new ConcurrentHashMap<>();

    public MessageSyncService(MessageProducer messageProducer,
                              ConversationService conversationService,
                              RedisTemplate<String, String> redisTemplate,
                              ImMessageBodyMapper imMessageBodyMapper,
                              RedisSeq redisSeq,
                              ImGroupMemberService imGroupMemberService,
                              GroupMessageProducer groupMessageProducer,
                              AppConfig appConfig) {
        this.messageProducer = messageProducer;
        this.conversationService = conversationService;
        this.redisTemplate = redisTemplate;
        this.imMessageBodyMapper = imMessageBodyMapper;
        this.redisSeq = redisSeq;
        this.imGroupMemberService = imGroupMemberService;
        this.groupMessageProducer = groupMessageProducer;
        this.appConfig = appConfig;
    }

    /**
     * Forward receive ACK from receiver to sender.
     *
     * @param messageReceiveAckContent receive ACK content
     */
    public void receiveMark(MessageReceiveAckContent messageReceiveAckContent){
        if (messageReceiveAckContent == null || StringUtils.isBlank(messageReceiveAckContent.getToId())) {
            logger.warn("receiveMark skipped: invalid content");
            return;
        }
        messageProducer.sendToUser(messageReceiveAckContent.getToId(),
                MessageCommand.MSG_RECIVE_ACK,messageReceiveAckContent,messageReceiveAckContent.getAppId());
    }

    /**
     * Handle P2P message read receipt.
     * <p>Updates conversation read sequence → notifies sender's other devices → sends read receipt to the message originator.</p>
     *
     * @param messageContent read receipt content
     */
    public void readMark(MessageReadedContent messageContent) {
        conversationService.messageMarkRead(messageContent);
        MessageReadedPack messageReadedPack = new MessageReadedPack();
        BeanUtils.copyProperties(messageContent,messageReadedPack);
        syncToSender(messageReadedPack,messageContent,MessageCommand.MSG_READED_NOTIFY);
        // Send read receipt to the message originator
        messageProducer.sendToUser(messageContent.getToId(),
                MessageCommand.MSG_READED_RECEIPT,messageReadedPack,messageContent.getAppId());
    }

    /**
     * Sync read receipt to sender's other online devices.
     *
     * @param pack    read receipt notification pack
     * @param content read receipt content
     * @param command command type
     */
    private void syncToSender(MessageReadedPack pack, MessageReadedContent content, Command command){
        // Send to sender's other devices (excluding current)
        messageProducer.sendToUserExceptClient(pack.getFromId(),
                command,pack,
                content);
    }

    /**
     * Handle group message read receipt.
     * <p>Updates conversation read sequence → syncs to sender's other devices → sends receipt to the message originator.</p>
     *
     * @param messageReaded read receipt content
     */
    public void groupReadMark(MessageReadedContent messageReaded) {
        conversationService.messageMarkRead(messageReaded);
        MessageReadedPack messageReadedPack = new MessageReadedPack();
        BeanUtils.copyProperties(messageReaded,messageReadedPack);
        syncToSender(messageReadedPack,messageReaded, GroupEventCommand.MSG_GROUP_READED_NOTIFY
        );
        if(!messageReaded.getFromId().equals(messageReaded.getToId())){
            messageProducer.sendToUser(messageReadedPack.getToId(),GroupEventCommand.MSG_GROUP_READED_RECEIPT
                    ,messageReaded,messageReaded.getAppId());
        }
    }

    /**
     * Sync offline messages (incremental pull).
     * <p>Fetches offline messages from Redis ZSet by sequence range. Falls back to empty list if Redis is unavailable.</p>
     *
     * @param req sync request (with lastSequence / maxLimit)
     * @return sync response (with offline message list and max sequence)
     */
    public Result syncOfflineMessage(SyncReq req) {

        SyncResp<OfflineMessageContent> resp = new SyncResp<>();

        // Boundary check: validate request parameters
        if (req == null || req.getAppId() == null || StringUtils.isBlank(req.getOperater())) {
            logger.warn("syncOfflineMessage skipped: invalid request (appId or operater is empty)");
            resp.setMaxSequence(0L);
            resp.setDataList(new ArrayList<>());
            resp.setCompleted(true);
            return Result.ok(resp);
        }

        // Clamp maxLimit to safe bounds [1, 500]
        if (req.getMaxLimit() == null || req.getMaxLimit() <= 0) {
            req.setMaxLimit(100);
        } else if (req.getMaxLimit() > 500) {
            req.setMaxLimit(500);
        }

        // Clamp lastSequence to non-negative
        if (req.getLastSequence() == null || req.getLastSequence() < 0) {
            req.setLastSequence(0L);
        }

        String key = req.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + req.getOperater();

        try {
            // Fetch the max sequence from the ZSet
            long maxSeq = 0L;
            ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
            Set<ZSetOperations.TypedTuple<String>> set = zSetOperations.reverseRangeWithScores(key, 0, 0);
            if (!CollectionUtils.isEmpty(set)) {
                ZSetOperations.TypedTuple<String> typedTuple = set.iterator().next();
                Double score = typedTuple.getScore();
                if (score != null) {
                    maxSeq = score.longValue();
                }
            }

            List<OfflineMessageContent> respList = new ArrayList<>();
            resp.setMaxSequence(maxSeq);

            // Query messages by score range (lastSequence, maxSeq]
            Set<ZSetOperations.TypedTuple<String>> querySet = zSetOperations.rangeByScoreWithScores(key,
                    req.getLastSequence(), maxSeq, 0, req.getMaxLimit());
            if (querySet != null) {
                for (ZSetOperations.TypedTuple<String> typedTuple : querySet) {
                    String value = typedTuple.getValue();
                    OfflineMessageContent offlineMessageContent = JSONObject.parseObject(value, OfflineMessageContent.class);
                    respList.add(offlineMessageContent);
                }
            }
            resp.setDataList(respList);

            if (!CollectionUtils.isEmpty(respList)) {
                OfflineMessageContent offlineMessageContent = respList.get(respList.size() - 1);
                resp.setCompleted(maxSeq <= offlineMessageContent.getMessageKey());
            }
        } catch (Exception e) {
            logger.error("Redis offline message sync failed, fallback to empty list, userId={}, error={}",
                    req.getOperater(), e.getMessage());
            // Fallback: return empty list, client will fetch from HTTP history
            resp.setMaxSequence(0L);
            resp.setDataList(new ArrayList<>());
            resp.setCompleted(true);
        }

        return Result.ok(resp);
    }

    /**
     * Handle message recall request.
     * <p>Flow: time boundary check (with clock skew tolerance) → concurrent conflict protection → query message body → mark deleted → update offline message status → notify sync targets and receiver.</p>
     *
     * @param content recall request content
     */
    public void recallMessage(RecallMessageContent content) {

        // Boundary check: validate recall request parameters
        if (content == null) {
            logger.warn("recallMessage skipped: content is null");
            return;
        }
        if (content.getMessageKey() == null || content.getMessageKey() <= 0) {
            logger.warn("recallMessage skipped: invalid messageKey");
            return;
        }
        if (content.getMessageTime() == null || content.getMessageTime() <= 0) {
            logger.warn("recallMessage skipped: invalid messageTime");
            return;
        }
        if (StringUtils.isBlank(content.getFromId()) || StringUtils.isBlank(content.getToId())) {
            logger.warn("recallMessage skipped: invalid fromId or toId");
            return;
        }

        Long messageTime = content.getMessageTime();
        Long now = System.currentTimeMillis();

        RecallMessageNotifyPack pack = new RecallMessageNotifyPack();
        BeanUtils.copyProperties(content,pack);

        // 1. Time boundary check: use configurable recall timeout + allow ±5s clock skew
        long recallTimeout = appConfig.getMessageRecallTimeOut() != null
                ? appConfig.getMessageRecallTimeOut() : 120000L;
        long clockSkewTolerance = 5000L; // 5s clock skew tolerance

        // Reject if client time is far ahead of server (possible clock desync)
        if (messageTime > now + clockSkewTolerance) {
            logger.warn("Client clock skew detected, messageTime={}, serverTime={}",
                    messageTime, now);
            recallAck(pack, Result.fail(MessageErrorCode.MESSAGE_CLOCK_SKEW_EXCEEDED), content);
            return;
        }

        // Check recall timeout against server time (authoritative)
        if (recallTimeout < now - messageTime) {
            logger.warn("Recall timed out, msgKey={}, messageTime={}, now={}, timeout={}",
                    content.getMessageKey(), messageTime, now, recallTimeout);
            recallAck(pack,Result.fail(MessageErrorCode.MESSAGE_RECALL_TIME_OUT),content);
            return;
        }

        // 2. Concurrent conflict protection: lock per messageKey to prevent double-recall
        Long messageKey = content.getMessageKey();
        Object lock = recallLocks.computeIfAbsent(messageKey, k -> new Object());
        synchronized (lock) {
            try {
                QueryWrapper<ImMessageBodyEntity> query = new QueryWrapper<>();
                query.eq("app_id",content.getAppId());
                query.eq("message_key", content.getMessageKey());
                ImMessageBodyEntity body = imMessageBodyMapper.selectOne(query);

                if(body == null){
                    logger.warn("Recall target not found, msgKey={}", content.getMessageKey());
                    recallAck(pack,Result.fail(MessageErrorCode.MESSAGEBODY_IS_NOT_EXIST),content);
                    return;
                }

                if(body.getDelFlag() == DelFlagEnum.DELETE.getCode()){
                    logger.warn("Message already recalled, msgKey={}", content.getMessageKey());
                    recallAck(pack,Result.fail(MessageErrorCode.MESSAGE_IS_RECALLED),content);
                    return;
                }

                body.setDelFlag(DelFlagEnum.DELETE.getCode());
                imMessageBodyMapper.update(body,query);

                if(content.getConversationType() == ConversationTypeEnum.P2P.getCode()){

                    // Build Redis keys for sender and receiver offline queues
                    String fromKey = content.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + content.getFromId();
                    String toKey = content.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + content.getToId();

                    OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                    BeanUtils.copyProperties(content,offlineMessageContent);
                    offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
                    offlineMessageContent.setMessageKey(content.getMessageKey());
                    offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
                    offlineMessageContent.setConversationId(conversationService.convertConversationId(offlineMessageContent.getConversationType()
                            ,content.getFromId(),content.getToId()));
                    offlineMessageContent.setMessageBody(body.getMessageBody());

                    long seq = redisSeq.doGetSeq(content.getAppId() + ":" + Constants.SeqConstants.Message + ":" + ConversationIdGenerate.generateP2PId(content.getFromId(),content.getToId()));
                    offlineMessageContent.setMessageSequence(seq);

                    long newMessageKey = SnowflakeIdWorker.nextId();

                    // Insert recall notification into offline queues
                    redisTemplate.opsForZSet().add(fromKey,JSONObject.toJSONString(offlineMessageContent),newMessageKey);
                    redisTemplate.opsForZSet().add(toKey,JSONObject.toJSONString(offlineMessageContent),newMessageKey);

                    // Send ACK to the recall initiator
                    recallAck(pack,Result.ok(),content);
                    // Sync to sender's other devices
                    messageProducer.sendToUserExceptClient(content.getFromId(),
                            MessageCommand.MSG_RECALL_NOTIFY,pack,content);
                    // Notify the receiver
                    messageProducer.sendToUser(content.getToId(),MessageCommand.MSG_RECALL_NOTIFY,
                            pack,content.getAppId());
                }else{
                    List<String> groupMemberId = imGroupMemberService.getGroupMemberId(content.getToId(), content.getAppId());
                    long seq = redisSeq.doGetSeq(content.getAppId() + ":" + Constants.SeqConstants.Message + ":" + ConversationIdGenerate.generateP2PId(content.getFromId(),content.getToId()));
                    // Send ACK to the recall initiator
                    recallAck(pack,Result.ok(),content);
                    // Sync to sender's other devices
                    messageProducer.sendToUserExceptClient(content.getFromId(), MessageCommand.MSG_RECALL_NOTIFY, pack
                            , content);
                    for (String memberId : groupMemberId) {
                        String toKey = content.getAppId() + ":" + Constants.SeqConstants.Message + ":" + memberId;
                        OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                        offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
                        BeanUtils.copyProperties(content,offlineMessageContent);
                        offlineMessageContent.setConversationType(ConversationTypeEnum.GROUP.getCode());
                        offlineMessageContent.setConversationId(conversationService.convertConversationId(offlineMessageContent.getConversationType()
                                ,content.getFromId(),content.getToId()));
                        offlineMessageContent.setMessageBody(body.getMessageBody());
                        offlineMessageContent.setMessageSequence(seq);
                        redisTemplate.opsForZSet().add(toKey,JSONObject.toJSONString(offlineMessageContent),seq);

                        groupMessageProducer.producer(content.getFromId(), MessageCommand.MSG_RECALL_NOTIFY, pack,content);
                    }
                }
            } finally {
                // Clean up lock to prevent memory leak
                recallLocks.remove(messageKey);
            }
        }
    }
    /**
     * Send recall result ACK to the request initiator.
     *
     * @param recallPack recall notification pack
     * @param success    response result
     * @param clientInfo client info
     */
    private void recallAck(RecallMessageNotifyPack recallPack, Result<Object> success, ClientInfo clientInfo) {
        messageProducer.sendToUser(recallPack.getFromId(),
                MessageCommand.MSG_RECALL_ACK, success, clientInfo);
    }

}
