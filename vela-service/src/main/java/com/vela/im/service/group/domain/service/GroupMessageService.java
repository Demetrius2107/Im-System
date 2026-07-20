package com.vela.im.service.group.domain.service;


import com.vela.im.service.group.application.dto.req.SendGroupMessageReq;
import com.vela.im.service.message.application.dto.resp.SendMessageResp;
import com.vela.im.service.message.domain.service.CheckSendMessageService;
import com.vela.im.service.message.domain.service.MessageStoreService;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.command.GroupEventCommand;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.shared.types.message.GroupChatMessageContent;
import com.vela.im.shared.types.message.MessageContent;
import com.vela.im.shared.types.message.OfflineMessageContent;
import com.vela.im.codec.pack.message.ChatMessageAck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Service
public class GroupMessageService {

    private static Logger logger = LoggerFactory.getLogger(GroupMessageService.class);

    @Autowired
    CheckSendMessageService checkSendMessageService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    @Autowired
    MessageStoreService messageStoreService;

    @Autowired
    RedisSeq redisSeq;

    private final ThreadPoolExecutor threadPoolExecutor;

    {
        AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("message-group-thread-" + num.getAndIncrement());
                return thread;
            }
        });
    }

    public void process(GroupChatMessageContent messageContent){
        String fromId = messageContent.getFromId();
        String groupId = messageContent.getGroupId();
        Integer appId = messageContent.getAppId();
        //前置校验
        //这个用户是否被禁言 是否被禁用
        //发送方和接收方是否是好友
        GroupChatMessageContent messageFromMessageIdCache = messageStoreService.getMessageFromMessageIdCache(messageContent.getAppId(),
                messageContent.getMessageId(), GroupChatMessageContent.class);
        if(messageFromMessageIdCache != null){
            threadPoolExecutor.execute(() ->{
                //1.回ack成功给自己
                ack(messageContent, ResponseVO.successResponse());
                //2.发消息给同步在线端
                syncToSender(messageContent,messageContent);
                //3.发消息给对方在线端
                dispatchMessage(messageContent);
            });
        }
        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.GroupMessage
                + messageContent.getGroupId());
        messageContent.setMessageSequence(seq);
            threadPoolExecutor.execute(() ->{
                messageStoreService.storeGroupMessage(messageContent);

                List<String> groupMemberId = imGroupMemberService.getGroupMemberId(messageContent.getGroupId(),
                        messageContent.getAppId());
                messageContent.setMemberId(groupMemberId);

                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyProperties(messageContent,offlineMessageContent);
                offlineMessageContent.setToId(messageContent.getGroupId());
                messageStoreService.storeGroupOfflineMessage(offlineMessageContent,groupMemberId);

                //1.回ack成功给自己
                ack(messageContent,ResponseVO.successResponse());
                //2.发消息给同步在线端
                syncToSender(messageContent,messageContent);
                //3.发消息给对方在线端
                dispatchMessage(messageContent);

                messageStoreService.setMessageFromMessageIdCache(messageContent.getAppId(),
                        messageContent.getMessageId(),messageContent);
            });
    }

    /**
     * 群聊消息分发，对每个成员发送，失败时重试并记录失败列表
     */
    private void dispatchMessage(GroupChatMessageContent messageContent){
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
                        logger.warn("群聊消息分发失败，memberId={}, msgId={}, 第{}次重试, error={}",
                                memberId, messageContent.getMessageId(), retry + 1, e.getMessage());
                        try {
                            Thread.sleep(100L * (1L << retry)); // 100ms, 200ms
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                if (!success) {
                    failedMembers.add(memberId);
                    logger.error("群聊消息分发重试耗尽，memberId={}, msgId={}",
                            memberId, messageContent.getMessageId());
                }
            }
        }
        if (!failedMembers.isEmpty()) {
            logger.warn("群聊消息部分成员分发失败，msgId={}, groupId={}, 失败成员数={}/{}",
                    messageContent.getMessageId(), messageContent.getGroupId(),
                    failedMembers.size(), messageContent.getMemberId().size() - 1);
        }
    }

    private void ack(MessageContent messageContent, ResponseVO responseVO){

        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        //發消息
        messageProducer.sendToUser(messageContent.getFromId(),
                GroupEventCommand.GROUP_MSG_ACK,
                responseVO,messageContent
        );
    }

    private void syncToSender(GroupChatMessageContent messageContent, ClientInfo clientInfo){
        messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                GroupEventCommand.MSG_GROUP,messageContent,messageContent);
    }

    private ResponseVO imServerPermissionCheck(String fromId, String toId,Integer appId){
        ResponseVO responseVO = checkSendMessageService
                .checkGroupMessage(fromId, toId,appId);
        return responseVO;
    }

    public SendMessageResp send(SendGroupMessageReq req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        GroupChatMessageContent message = new GroupChatMessageContent();
        BeanUtils.copyProperties(req,message);

        messageStoreService.storeGroupMessage(message);

        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        //2.发消息给同步在线端
        syncToSender(message,message);
        //3.发消息给对方在线端
        dispatchMessage(message);

        return sendMessageResp;

    }
}
