package com.vela.im.service.message.domain.service;

import com.vela.im.service.message.application.dto.req.SendMessageReq;
import com.vela.im.service.message.application.dto.resp.SendMessageResp;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.application.utils.CallbackService;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.shared.types.enums.FriendShipErrorCode;
import com.vela.im.shared.types.enums.MessageErrorCode;
import com.vela.im.shared.types.enums.command.MessageCommand;
import com.vela.im.shared.types.message.MessageContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <p>Title: P2PMessageServiceTest</p>
 * <p>Description: P2PMessageService 单元测试，覆盖消息处理、权限校验、REST API 发送等核心路径</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-24
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("P2PMessageService - 单聊消息服务")
class P2PMessageServiceTest {

    @Mock
    private CheckSendMessageService checkSendMessageService;
    @Mock
    private MessageProducer messageProducer;
    @Mock
    private MessageStoreService messageStoreService;
    @Mock
    private RedisSeq redisSeq;
    @Mock
    private AppConfig appConfig;
    @Mock
    private CallbackService callbackService;

    @Captor
    private ArgumentCaptor<MessageContent> messageCaptor;

    private P2PMessageService service;

    @BeforeEach
    void setUp() {
        service = new P2PMessageService(checkSendMessageService, messageProducer,
                messageStoreService, redisSeq, appConfig, callbackService);
    }

    @Nested
    @DisplayName("imServerPermissionCheck - 发送权限校验")
    class PermissionCheckTest {

        private final String FROM_ID = "user001";
        private final String TO_ID = "user002";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("发送方正常且好友关系正常应返回成功")
        void normalUserAndFriendReturnsOk() {
            when(checkSendMessageService.checkSenderForvidAndMute(FROM_ID, APP_ID))
                    .thenReturn(Result.ok());
            when(checkSendMessageService.checkFriendShip(FROM_ID, TO_ID, APP_ID))
                    .thenReturn(Result.ok());

            Result result = service.imServerPermissionCheck(FROM_ID, TO_ID, APP_ID);

            assertTrue(result.isOk());
        }

        @Test
        @DisplayName("发送方被禁言应返回禁言错误")
        void mutedUserReturnsError() {
            when(checkSendMessageService.checkSenderForvidAndMute(FROM_ID, APP_ID))
                    .thenReturn(Result.fail(MessageErrorCode.FROMER_IS_MUTE));

            Result result = service.imServerPermissionCheck(FROM_ID, TO_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(MessageErrorCode.FROMER_IS_MUTE.getCode(), result.getCode());
            verify(checkSendMessageService, never()).checkFriendShip(any(), any(), any());
        }

        @Test
        @DisplayName("发送方被禁用应返回禁用错误")
        void forbiddenUserReturnsError() {
            when(checkSendMessageService.checkSenderForvidAndMute(FROM_ID, APP_ID))
                    .thenReturn(Result.fail(MessageErrorCode.FROMER_IS_FORBIBBEN));

            Result result = service.imServerPermissionCheck(FROM_ID, TO_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(MessageErrorCode.FROMER_IS_FORBIBBEN.getCode(), result.getCode());
        }

        @Test
        @DisplayName("好友关系异常应返回好友错误")
        void notFriendReturnsError() {
            when(checkSendMessageService.checkSenderForvidAndMute(FROM_ID, APP_ID))
                    .thenReturn(Result.ok());
            when(checkSendMessageService.checkFriendShip(FROM_ID, TO_ID, APP_ID))
                    .thenReturn(Result.fail(FriendShipErrorCode.FRIEND_IS_DELETED));

            Result result = service.imServerPermissionCheck(FROM_ID, TO_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(FriendShipErrorCode.FRIEND_IS_DELETED.getCode(), result.getCode());
        }
    }

    @Nested
    @DisplayName("process - 消息处理（同步校验路径）")
    class ProcessTest {

        private final String FROM_ID = "user001";
        private final String TO_ID = "user002";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("fromId 为空应拒绝并返回错误ACK")
        void emptyFromIdRejected() {
            MessageContent msg = createMessage("", TO_ID, APP_ID, "hello");

            service.process(msg);

            // After process returns, the ACK is sent synchronously (before thread pool)
            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(""), eq(MessageCommand.MSG_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("toId 为空应拒绝并返回错误ACK")
        void emptyToIdRejected() {
            MessageContent msg = createMessage(FROM_ID, "", APP_ID, "hello");

            service.process(msg);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("消息体为空应拒绝")
        void emptyBodyRejected() {
            lenient().when(appConfig.getMessageMaxSize()).thenReturn(65536);
            MessageContent msg = createMessage(FROM_ID, TO_ID, APP_ID, "");

            service.process(msg);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("发送给自己应拒绝")
        void selfSendRejected() {
            MessageContent msg = createMessage(FROM_ID, FROM_ID, APP_ID, "hello");

            service.process(msg);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_ACK), any(), any(ClientInfo.class));
        }

        @Test
        @DisplayName("消息体超过大小限制应拒绝")
        void oversizedBodyRejected() {
            when(appConfig.getMessageMaxSize()).thenReturn(5);
            MessageContent msg = createMessage(FROM_ID, TO_ID, APP_ID, "hello world");

            service.process(msg);

            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(FROM_ID), eq(MessageCommand.MSG_ACK), any(), any(ClientInfo.class));
        }

        private MessageContent createMessage(String fromId, String toId, Integer appId, String body) {
            MessageContent msg = new MessageContent();
            msg.setMessageId("msg-" + System.nanoTime());
            msg.setFromId(fromId);
            msg.setToId(toId);
            msg.setAppId(appId);
            msg.setMessageBody(body);
            msg.setMessageTime(System.currentTimeMillis());
            return msg;
        }
    }

    @Nested
    @DisplayName("send - REST API 同步发送")
    class SendTest {

        private final String FROM_ID = "user001";
        private final String TO_ID = "user002";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("正常发送应返回带有 messageKey 和 messageTime 的响应")
        void normalSendReturnsResp() {
            SendMessageReq req = new SendMessageReq();
            req.setFromId(FROM_ID);
            req.setToId(TO_ID);
            req.setAppId(APP_ID);
            req.setMessageBody("hello");

            // messageStoreService.storeP2PMessage sets messageKey on the content
            doAnswer(invocation -> {
                MessageContent arg = invocation.getArgument(0);
                arg.setMessageKey(12345L);
                return null;
            }).when(messageStoreService).storeP2PMessage(any(MessageContent.class));

            SendMessageResp resp = service.send(req);

            assertNotNull(resp);
            assertEquals(12345L, resp.getMessageKey());
            assertTrue(resp.getMessageTime() > 0);
            // Verify dispatch was called
            verify(messageProducer, timeout(1000)).sendToUser(
                    eq(TO_ID), eq(MessageCommand.MSG_P2P), any(), eq(APP_ID));
        }
    }
}
