package com.vela.im.service.message.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vela.im.service.conversation.domain.service.ConversationService;
import com.vela.im.service.group.domain.service.ImGroupMemberService;
import com.vela.im.service.message.domain.entity.ImMessageBodyEntity;
import com.vela.im.service.message.infrastructure.persistence.mapper.ImMessageBodyMapper;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.application.utils.GroupMessageProducer;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.shared.types.enums.*;
import com.vela.im.shared.types.enums.command.MessageCommand;
import com.vela.im.shared.types.message.RecallMessageContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <p>Title: MessageSyncServiceTest</p>
 * <p>Description: MessageSyncService 单元测试，聚焦消息撤回的异常边界场景</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-24
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MessageSyncService - 消息同步与撤回")
class MessageSyncServiceTest {

    @Mock
    private MessageProducer messageProducer;
    @Mock
    private ConversationService conversationService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ImMessageBodyMapper imMessageBodyMapper;
    @Mock
    private RedisSeq redisSeq;
    @Mock
    private ImGroupMemberService imGroupMemberService;
    @Mock
    private GroupMessageProducer groupMessageProducer;
    @Mock
    private AppConfig appConfig;

    private MessageSyncService service;

    @BeforeEach
    void setUp() {
        service = new MessageSyncService(messageProducer, conversationService,
                redisTemplate, imMessageBodyMapper, redisSeq, imGroupMemberService,
                groupMessageProducer, appConfig);
    }

    @Nested
    @DisplayName("recallMessage - 消息撤回边界校验")
    class RecallMessageTest {

        private final String FROM_ID = "user001";
        private final String TO_ID = "user002";
        private final Integer APP_ID = 100;
        private final Long MESSAGE_KEY = 12345L;

        @Test
        @DisplayName("content 为 null 应静默跳过")
        void nullContentSilentlySkips() {
            service.recallMessage(null);

            verify(imMessageBodyMapper, never()).selectOne(any());
            verify(messageProducer, never()).sendToUser(anyString(), any(), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("messageKey 为空应跳过")
        void nullMessageKeySkips() {
            RecallMessageContent content = createRecallContent();
            content.setMessageKey(null);

            service.recallMessage(content);

            verify(imMessageBodyMapper, never()).selectOne(any());
        }

        @Test
        @DisplayName("messageKey <= 0 应跳过")
        void invalidMessageKeySkips() {
            RecallMessageContent content = createRecallContent();
            content.setMessageKey(0L);

            service.recallMessage(content);

            verify(imMessageBodyMapper, never()).selectOne(any());
        }

        @Test
        @DisplayName("messageTime 为 null 应跳过")
        void nullMessageTimeSkips() {
            RecallMessageContent content = createRecallContent();
            content.setMessageTime(null);

            service.recallMessage(content);

            verify(imMessageBodyMapper, never()).selectOne(any());
        }

        @Test
        @DisplayName("fromId 为空应跳过")
        void emptyFromIdSkips() {
            RecallMessageContent content = createRecallContent();
            content.setFromId("");

            service.recallMessage(content);

            verify(imMessageBodyMapper, never()).selectOne(any());
        }

        @Test
        @DisplayName("客户端时钟偏差过大应拒绝（clientTime > now + 5s）")
        void clockSkewExceededRejected() {
            RecallMessageContent content = createRecallContent();
            content.setMessageTime(System.currentTimeMillis() + 10000); // 10s in the future
            when(appConfig.getMessageRecallTimeOut()).thenReturn(120000L);

            service.recallMessage(content);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_RECALL_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("撤回超时应拒绝（messageTime > recallTimeout 前）")
        void recallTimeoutExceededRejected() {
            RecallMessageContent content = createRecallContent();
            content.setMessageTime(System.currentTimeMillis() - 180000); // 3min ago
            when(appConfig.getMessageRecallTimeOut()).thenReturn(120000L); // 2min timeout

            service.recallMessage(content);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_RECALL_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("消息体不存在应拒绝")
        void messageBodyNotFoundRejected() {
            RecallMessageContent content = createRecallContent();
            when(appConfig.getMessageRecallTimeOut()).thenReturn(120000L);
            when(imMessageBodyMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

            service.recallMessage(content);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_RECALL_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("消息已被撤回应拒绝")
        void alreadyRecalledMessageRejected() {
            RecallMessageContent content = createRecallContent();
            when(appConfig.getMessageRecallTimeOut()).thenReturn(120000L);

            ImMessageBodyEntity body = new ImMessageBodyEntity();
            body.setMessageKey(MESSAGE_KEY);
            body.setDelFlag(DelFlagEnum.DELETE.getCode()); // already deleted
            when(imMessageBodyMapper.selectOne(any(QueryWrapper.class))).thenReturn(body);

            service.recallMessage(content);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_RECALL_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("P2P 消息撤回成功应发送 ACK 并通知双方")
        void successfulP2pRecallSendsAckAndNotify() {
            RecallMessageContent content = createRecallContent();
            content.setConversationType(ConversationTypeEnum.P2P.getCode());
            when(appConfig.getMessageRecallTimeOut()).thenReturn(120000L);
            when(redisSeq.doGetSeq(anyString())).thenReturn(200L);

            ImMessageBodyEntity body = new ImMessageBodyEntity();
            body.setMessageKey(MESSAGE_KEY);
            body.setDelFlag(DelFlagEnum.NORMAL.getCode());
            body.setMessageBody("original message body");
            when(imMessageBodyMapper.selectOne(any(QueryWrapper.class))).thenReturn(body);

            ZSetOperations<String, String> zset = mock(ZSetOperations.class);
            when(redisTemplate.opsForZSet()).thenReturn(zset);

            service.recallMessage(content);

            // Should send ACK to recall initiator
            verify(messageProducer, atLeastOnce()).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_RECALL_ACK), any(), any(ClientInfo.class));
            // Should send recall notify to receiver
            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(TO_ID), eq(MessageCommand.MSG_RECALL_NOTIFY), any(), eq(APP_ID));
            // Should sync to sender's other devices
            verify(messageProducer, timeout(1000)).sendToUserExceptClient(
                    eq(FROM_ID), eq(MessageCommand.MSG_RECALL_NOTIFY), any(), any());
            // Should mark as deleted in DB
            verify(imMessageBodyMapper, times(1)).update(
                    argThat(bodyEntity -> bodyEntity.getDelFlag() == DelFlagEnum.DELETE.getCode()),
                    any(QueryWrapper.class));
        }

        private RecallMessageContent createRecallContent() {
            RecallMessageContent content = new RecallMessageContent();
            content.setAppId(APP_ID);
            content.setFromId(FROM_ID);
            content.setToId(TO_ID);
            content.setMessageKey(MESSAGE_KEY);
            content.setMessageTime(System.currentTimeMillis());
            content.setConversationType(ConversationTypeEnum.P2P.getCode());
            return content;
        }
    }
}
