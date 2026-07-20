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
import com.vela.im.shared.base.ResponseVO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Service
public class MessageSyncService {

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    ConversationService conversationService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ImMessageBodyMapper imMessageBodyMapper;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    @Autowired
    GroupMessageProducer groupMessageProducer;

    @Autowired
    AppConfig appConfig;

    private static Logger logger = LoggerFactory.getLogger(MessageSyncService.class);

    /** 消息撤回并发锁，防止同一消息被多次撤回 */
    private final ConcurrentHashMap<Long, Object> recallLocks = new ConcurrentHashMap<>();


    public void receiveMark(MessageReceiveAckContent messageReceiveAckContent){
        messageProducer.sendToUser(messageReceiveAckContent.getToId(),
                MessageCommand.MSG_RECIVE_ACK,messageReceiveAckContent,messageReceiveAckContent.getAppId());
    }

    /**
     * @description: 消息已读。更新会话的seq，通知在线的同步端发送指定command ，发送已读回执通知对方（消息发起方）我已读
     * @param
     * @return void
     * @author wanqiu
     */
    public void readMark(MessageReadedContent messageContent) {
        conversationService.messageMarkRead(messageContent);
        MessageReadedPack messageReadedPack = new MessageReadedPack();
        BeanUtils.copyProperties(messageContent,messageReadedPack);
        syncToSender(messageReadedPack,messageContent,MessageCommand.MSG_READED_NOTIFY);
        //发送给对方
        messageProducer.sendToUser(messageContent.getToId(),
                MessageCommand.MSG_READED_RECEIPT,messageReadedPack,messageContent.getAppId());
    }

    private void syncToSender(MessageReadedPack pack, MessageReadedContent content, Command command){
        MessageReadedPack messageReadedPack = new MessageReadedPack();
//        BeanUtils.copyProperties(messageReadedContent,messageReadedPack);
        //发送给自己的其他端
        messageProducer.sendToUserExceptClient(pack.getFromId(),
                command,pack,
                content);
    }

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

    public ResponseVO syncOfflineMessage(SyncReq req) {

        SyncResp<OfflineMessageContent> resp = new SyncResp<>();

        String key = req.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + req.getOperater();
        //获取最大的seq
        Long maxSeq = 0L;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set set = zSetOperations.reverseRangeWithScores(key, 0, 0);
        if(!CollectionUtils.isEmpty(set)){
            List list = new ArrayList(set);
            DefaultTypedTuple o = (DefaultTypedTuple) list.get(0);
            maxSeq = o.getScore().longValue();
        }

        List<OfflineMessageContent> respList = new ArrayList<>();
        resp.setMaxSequence(maxSeq);

        Set<ZSetOperations.TypedTuple> querySet = zSetOperations.rangeByScoreWithScores(key,
                req.getLastSequence(), maxSeq, 0, req.getMaxLimit());
        for (ZSetOperations.TypedTuple<String> typedTuple : querySet) {
            String value = typedTuple.getValue();
            OfflineMessageContent offlineMessageContent = JSONObject.parseObject(value, OfflineMessageContent.class);
            respList.add(offlineMessageContent);
        }
        resp.setDataList(respList);

        if(!CollectionUtils.isEmpty(respList)){
            OfflineMessageContent offlineMessageContent = respList.get(respList.size() - 1);
            resp.setCompleted(maxSeq <= offlineMessageContent.getMessageKey());
        }

        return ResponseVO.successResponse(resp);
    }

    //修改历史消息的状态
    //修改离线消息的状态
    //ack给发送方
    //发送给同步端
    //分发给消息的接收方
    public void recallMessage(RecallMessageContent content) {

        Long messageTime = content.getMessageTime();
        Long now = System.currentTimeMillis();

        RecallMessageNotifyPack pack = new RecallMessageNotifyPack();
        BeanUtils.copyProperties(content,pack);

        // 1. 时间边界容错：使用配置化的撤回时间窗口 + 允许客户端时钟偏差（±5s）
        long recallTimeout = appConfig.getMessageRecallTimeOut() != null
                ? appConfig.getMessageRecallTimeOut() : 120000L;
        long clockSkewTolerance = 5000L; // 5s 时钟偏差容忍

        // 检查客户端时间是否在合理范围内（防止客户端时钟严重偏离）
        if (messageTime > now + clockSkewTolerance) {
            logger.warn("客户端时间超前过多，可能时钟不同步，messageTime={}, serverTime={}",
                    messageTime, now);
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_CLOCK_SKEW_EXCEEDED), content);
            return;
        }

        // 以服务端时间为准判断是否超时
        if (recallTimeout < now - messageTime) {
            logger.warn("消息撤回超时，msgKey={}, messageTime={}, now={}, timeout={}",
                    content.getMessageKey(), messageTime, now, recallTimeout);
            recallAck(pack,ResponseVO.errorResponse(MessageErrorCode.MESSAGE_RECALL_TIME_OUT),content);
            return;
        }

        // 2. 并发冲突保护：按 messageKey 粒度加锁，防止同一消息被多次撤回
        Long messageKey = content.getMessageKey();
        Object lock = recallLocks.computeIfAbsent(messageKey, k -> new Object());
        synchronized (lock) {
            try {
                QueryWrapper<ImMessageBodyEntity> query = new QueryWrapper<>();
                query.eq("app_id",content.getAppId());
                query.eq("message_key", content.getMessageKey());
                ImMessageBodyEntity body = imMessageBodyMapper.selectOne(query);

                if(body == null){
                    logger.warn("撤回消息不存在，msgKey={}", content.getMessageKey());
                    recallAck(pack,ResponseVO.errorResponse(MessageErrorCode.MESSAGEBODY_IS_NOT_EXIST),content);
                    return;
                }

                if(body.getDelFlag() == DelFlagEnum.DELETE.getCode()){
                    logger.warn("消息已被撤回，msgKey={}", content.getMessageKey());
                    recallAck(pack,ResponseVO.errorResponse(MessageErrorCode.MESSAGE_IS_RECALLED),content);
                    return;
                }

                body.setDelFlag(DelFlagEnum.DELETE.getCode());
                imMessageBodyMapper.update(body,query);

                if(content.getConversationType() == ConversationTypeEnum.P2P.getCode()){

                    // 找到fromId的队列
                    String fromKey = content.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + content.getFromId();
                    // 找到toId的队列
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

                    redisTemplate.opsForZSet().add(fromKey,JSONObject.toJSONString(offlineMessageContent),newMessageKey);
                    redisTemplate.opsForZSet().add(toKey,JSONObject.toJSONString(offlineMessageContent),newMessageKey);

                    //ack
                    recallAck(pack,ResponseVO.successResponse(),content);
                    //分发给同步端
                    messageProducer.sendToUserExceptClient(content.getFromId(),
                            MessageCommand.MSG_RECALL_NOTIFY,pack,content);
                    //分发给对方
                    messageProducer.sendToUser(content.getToId(),MessageCommand.MSG_RECALL_NOTIFY,
                            pack,content.getAppId());
                }else{
                    List<String> groupMemberId = imGroupMemberService.getGroupMemberId(content.getToId(), content.getAppId());
                    long seq = redisSeq.doGetSeq(content.getAppId() + ":" + Constants.SeqConstants.Message + ":" + ConversationIdGenerate.generateP2PId(content.getFromId(),content.getToId()));
                    //ack
                    recallAck(pack,ResponseVO.successResponse(),content);
                    //发送给同步端
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
                // 清理锁，避免内存泄漏
                recallLocks.remove(messageKey);
            }
        }
    }
    private void recallAck(RecallMessageNotifyPack recallPack, ResponseVO<Object> success, ClientInfo clientInfo) {
        ResponseVO<Object> wrappedResp = success;
        messageProducer.sendToUser(recallPack.getFromId(),
                MessageCommand.MSG_RECALL_ACK, wrappedResp, clientInfo);
    }

}
