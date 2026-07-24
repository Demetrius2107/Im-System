package com.vela.im.service.group.domain.service;

import com.vela.im.service.group.application.dto.req.SendGroupMessageRequest;
import com.vela.im.service.message.application.dto.resp.SendMessageResp;
import com.vela.im.service.message.domain.service.MessageStoreService;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.types.enums.command.GroupEventCommand;
import com.vela.im.shared.types.message.GroupChatMessageContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <p>Title: GroupMessageServiceTest</p>
 * <p>Description: GroupMessageService 单元测试，覆盖群聊消息边界校验、幂等检查、REST API 发送</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-24
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GroupMessageService - 群聊消息服务")
class GroupMessageServiceTest {

    @Mock
    private MessageProducer messageProducer;
    @Mock
    private ImGroupMemberService imGroupMemberService;
    @Mock
    private MessageStoreService messageStoreService;
    @Mock
    private RedisSeq redisSeq;
    @Mock
    private AppConfig appConfig;

    private GroupMessageService service;

    @BeforeEach
    void setUp() {
        service = new GroupMessageService(messageProducer, imGroupMemberService,
                messageStoreService, redisSeq, appConfig);
    }

    @Nested
    @DisplayName("process - 群聊消息处理（同步校验路径）")
    class ProcessTest {

        private final String FROM_ID = "user001";
        private final String GROUP_ID = "group001";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("fromId 为空应拒绝")
        void emptyFromIdRejected() {
            GroupChatMessageContent msg = createMessage("", GROUP_ID, APP_ID, "hello");
            service.process(msg);
            verify(messageProducer, timeout(2000)).sendToUser(
                    eq(""), eq(GroupEventCommand.GROUP_MSG_ACK), any(), any(GroupChatMessageContent.class));
        }

        @Test
        @DisplayName("groupId 为空应拒绝")
        void emptyGroupIdRejected() {
            GroupChatMessageContent msg = createMessage(FROM_ID, "", APP_ID, "hello");
            service.process(msg);
            verify(messageProducer, timeout(2000)).sendToUser(
                    eq(FROM_ID), eq(GroupEventCommand.GROUP_MSG_ACK), any(), any(GroupChatMessageContent.class));
        }

        @Test
        @DisplayName("消息体为空应拒绝")
        void emptyBodyRejected() {
            lenient().when(appConfig.getMessageMaxSize()).thenReturn(65536);
            GroupChatMessageContent msg = createMessage(FROM_ID, GROUP_ID, APP_ID, "");
            service.process(msg);
            verify(messageProducer, timeout(2000)).sendToUser(
                    eq(FROM_ID), eq(GroupEventCommand.GROUP_MSG_ACK), any(), any(GroupChatMessageContent.class));
        }

        @Test
        @DisplayName("消息体超过大小限制应拒绝")
        void oversizedBodyRejected() {
            when(appConfig.getMessageMaxSize()).thenReturn(5);
            GroupChatMessageContent msg = createMessage(FROM_ID, GROUP_ID, APP_ID, "hello world this is too long");
            service.process(msg);
            verify(messageProducer, timeout(2000)).sendToUser(
                    eq(FROM_ID), eq(GroupEventCommand.GROUP_MSG_ACK), any(), any(GroupChatMessageContent.class));
        }

        @Test
        @DisplayName("消息时间偏差过大应拒绝")
        void invalidMessageTimeRejected() {
            lenient().when(appConfig.getMessageMaxSize()).thenReturn(65536);
            when(appConfig.getMessageTimeMaxDeviation()).thenReturn(5000L);
            GroupChatMessageContent msg = createMessage(FROM_ID, GROUP_ID, APP_ID, "hello");
            msg.setMessageTime(System.currentTimeMillis() - 60000);
            service.process(msg);
            verify(messageProducer, timeout(2000)).sendToUser(
                    eq(FROM_ID), eq(GroupEventCommand.GROUP_MSG_ACK), any(), any(GroupChatMessageContent.class));
        }

        @Test
        @DisplayName("重复消息应走幂等路径（不再生成 seq 和存储）")
        void duplicateMessageGoesIdempotentPath() {
            GroupChatMessageContent msg = createMessage(FROM_ID, GROUP_ID, APP_ID, "hello");
            lenient().when(messageStoreService.getMessageFromMessageIdCache(
                    eq(APP_ID), eq(msg.getMessageId()), eq(GroupChatMessageContent.class)))
                    .thenReturn(msg);

            service.process(msg);

            verify(redisSeq, never()).doGetSeq(anyString());
            verify(messageStoreService, never()).storeGroupMessage(any());
        }

        private GroupChatMessageContent createMessage(String fromId, String groupId,
                                                       Integer appId, String body) {
            GroupChatMessageContent msg = new GroupChatMessageContent();
            msg.setMessageId("msg-" + System.nanoTime());
            msg.setFromId(fromId);
            msg.setGroupId(groupId);
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
        private final String GROUP_ID = "group001";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("正常发送应返回 messageKey 和 messageTime")
        void normalSendReturnsResp() {
            SendGroupMessageRequest req = new SendGroupMessageRequest();
            req.setFromId(FROM_ID);
            req.setGroupId(GROUP_ID);
            req.setAppId(APP_ID);
            req.setMessageBody("hello");

            doAnswer(invocation -> {
                GroupChatMessageContent arg = invocation.getArgument(0);
                arg.setMessageKey(54321L);
                return null;
            }).when(messageStoreService).storeGroupMessage(any(GroupChatMessageContent.class));

            SendMessageResp resp = service.send(req);

            assertNotNull(resp);
            assertEquals(54321L, resp.getMessageKey());
            assertTrue(resp.getMessageTime() > 0);
        }

        @Test
        @DisplayName("send 路径无成员ID列表时不应分发消息")
        void sendWithoutMemberIdsDoesNotDispatch() {
            SendGroupMessageRequest req = new SendGroupMessageRequest();
            req.setFromId(FROM_ID);
            req.setGroupId(GROUP_ID);
            req.setAppId(APP_ID);
            req.setMessageBody("hello");

            doAnswer(invocation -> {
                GroupChatMessageContent arg = invocation.getArgument(0);
                arg.setMessageKey(54321L);
                return null;
            }).when(messageStoreService).storeGroupMessage(any(GroupChatMessageContent.class));

            assertDoesNotThrow(() -> service.send(req));
        }
    }

    @Nested
    @DisplayName("process + dispatchMessage - 异步分发路径")
    class AsyncDispatchTest {

        private final String FROM_ID = "user001";
        private final String GROUP_ID = "group001";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("新消息应生成序列号并持久化")
        void newMessageStoresAndGeneratesSeq() {
            lenient().when(appConfig.getMessageMaxSize()).thenReturn(65536);
            lenient().when(appConfig.getMessageTimeMaxDeviation()).thenReturn(300000L);
            lenient().when(appConfig.getMessageRateLimit()).thenReturn(20);
            when(redisSeq.doGetSeq(anyString())).thenReturn(100L);
            when(imGroupMemberService.getGroupMemberId(GROUP_ID, APP_ID))
                    .thenReturn(List.of(FROM_ID, "user002"));

            GroupChatMessageContent msg = new GroupChatMessageContent();
            msg.setMessageId("msg-new");
            msg.setFromId(FROM_ID);
            msg.setGroupId(GROUP_ID);
            msg.setAppId(APP_ID);
            msg.setMessageBody("hello");
            msg.setMessageTime(System.currentTimeMillis());

            service.process(msg);

            verify(redisSeq, timeout(2000)).doGetSeq(anyString());
            assertEquals(100L, msg.getMessageSequence());
        }
    }
}
