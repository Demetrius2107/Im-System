package com.vela.im.service.message.domain.service;

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
import com.vela.im.shared.types.message.GroupChatMessageContent;
import com.vela.im.shared.types.message.MessageContent;
import com.vela.im.shared.types.message.OfflineMessageContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.data.redis.core.ValueOperations;

/**
 * <p>Title: MessageStoreServiceTest</p>
 * <p>Description: MessageStoreService 单元测试，覆盖 MQ 降级、Redis 降级、ZSet 超限驱逐等异常边界</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-24
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MessageStoreService - 消息存储与降级")
class MessageStoreServiceTest {

    @Mock
    private ImMessageHistoryMapper imMessageHistoryMapper;
    @Mock
    private ImMessageBodyMapper imMessageBodyMapper;
    @Mock
    private SnowflakeIdWorker snowflakeIdWorker;
    @Mock
    private ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ConversationService conversationService;
    @Mock
    private AppConfig appConfig;

    private MessageStoreService service;

    @BeforeEach
    void setUp() {
        service = new MessageStoreService(imMessageHistoryMapper, imMessageBodyMapper,
                snowflakeIdWorker, imGroupMessageHistoryMapper, rabbitTemplate,
                stringRedisTemplate, conversationService, appConfig);
    }

    @Nested
    @DisplayName("storeP2PMessage - 单聊消息存储降级")
    class StoreP2PMessageTest {

        @Test
        @DisplayName("MQ 正常时消息通过 MQ 发送，不直接写 DB")
        void mqSuccessDoesNotWriteDbDirectly() {
            MessageContent msg = createP2PMessage();

            service.storeP2PMessage(msg);

            verify(rabbitTemplate, times(1)).convertAndSend(
                    eq(Constants.RabbitConstants.StoreP2PMessage), eq(""), anyString(), any(MessagePostProcessor.class));
            verify(imMessageBodyMapper, never()).insert(any());
        }

        @Test
        @DisplayName("MQ 发送失败时自动降级写入 DB")
        void mqFailureTriggersDirectDbWrite() {
            MessageContent msg = createP2PMessage();
            doThrow(new RuntimeException("MQ connection refused"))
                    .when(rabbitTemplate).convertAndSend(
                            eq(Constants.RabbitConstants.StoreP2PMessage), eq(""), anyString(), any(MessagePostProcessor.class));

            service.storeP2PMessage(msg);

            // MQ was attempted
            verify(rabbitTemplate, times(1)).convertAndSend(
                    eq(Constants.RabbitConstants.StoreP2PMessage), eq(""), anyString(), any(MessagePostProcessor.class));
            // Fallback writes to DB
            verify(imMessageBodyMapper, atLeastOnce()).insert(any(ImMessageBodyEntity.class));
        }

        private MessageContent createP2PMessage() {
            MessageContent msg = new MessageContent();
            msg.setAppId(100);
            msg.setFromId("user001");
            msg.setToId("user002");
            msg.setMessageBody("hello");
            msg.setMessageTime(System.currentTimeMillis());
            return msg;
        }
    }

    @Nested
    @DisplayName("storeGroupMessage - 群聊消息存储降级")
    class StoreGroupMessageTest {

        @Test
        @DisplayName("MQ 失败时自动降级写入 DB")
        void mqFailureTriggersDirectDbWrite() {
            GroupChatMessageContent msg = createGroupMessage();
            doThrow(new RuntimeException("MQ timeout"))
                    .when(rabbitTemplate).convertAndSend(
                            eq(Constants.RabbitConstants.StoreGroupMessage), eq(""), anyString(), any(MessagePostProcessor.class));

            service.storeGroupMessage(msg);

            verify(rabbitTemplate, times(1)).convertAndSend(
                    eq(Constants.RabbitConstants.StoreGroupMessage), eq(""), anyString(), any(MessagePostProcessor.class));
            verify(imMessageBodyMapper, atLeastOnce()).insert(any(ImMessageBodyEntity.class));
        }

        @Test
        @DisplayName("MQ 正常时群聊消息不走直接 DB")
        void mqSuccessDoesNotWriteDirectly() {
            GroupChatMessageContent msg = createGroupMessage();

            service.storeGroupMessage(msg);

            verify(rabbitTemplate, times(1)).convertAndSend(
                    eq(Constants.RabbitConstants.StoreGroupMessage), eq(""), anyString(), any(MessagePostProcessor.class));
            verify(imMessageBodyMapper, never()).insert(any());
        }

        private GroupChatMessageContent createGroupMessage() {
            GroupChatMessageContent msg = new GroupChatMessageContent();
            msg.setAppId(100);
            msg.setFromId("user001");
            msg.setGroupId("group001");
            msg.setMessageBody("hello group");
            msg.setMessageTime(System.currentTimeMillis());
            return msg;
        }
    }

    @Nested
    @DisplayName("storeOfflineMessage - 单聊离线消息 Redis 降级")
    class StoreOfflineMessageTest {

        @Test
        @DisplayName("Redis 正常时消息写入 ZSet")
        void redisSuccessWritesToZSet() {
            OfflineMessageContent msg = createOfflineMsg();
            ZSetOperations<String, String> zset = mock(ZSetOperations.class);
            when(stringRedisTemplate.opsForZSet()).thenReturn(zset);
            when(zset.zCard(anyString())).thenReturn(10L);
            when(appConfig.getOfflineMessageCount()).thenReturn(500);

            service.storeOfflineMessage(msg);

            verify(zset, atLeast(2)).add(anyString(), anyString(), anyDouble());
            verify(imMessageHistoryMapper, never()).insert(any());
        }

        @Test
        @DisplayName("Redis 连接失败时降级写入 DB")
        void redisFailureFallsBackToDb() {
            OfflineMessageContent msg = createOfflineMsg();
            ZSetOperations<String, String> zset = mock(ZSetOperations.class);
            when(stringRedisTemplate.opsForZSet()).thenReturn(zset);
            // Simulate Redis failure on first ZSet operation
            when(zset.zCard(anyString())).thenThrow(new RuntimeException("Redis connection refused"));

            service.storeOfflineMessage(msg);

            // Fallback persists to message history table
            verify(imMessageHistoryMapper, atLeastOnce()).insert(any(ImMessageHistoryEntity.class));
        }

        @Test
        @DisplayName("ZSet 容量超限时驱逐最旧消息到 DB")
        void zsetExceededEvictsOldestToDb() {
            OfflineMessageContent msg = createOfflineMsg();
            ZSetOperations<String, String> zset = mock(ZSetOperations.class);
            when(stringRedisTemplate.opsForZSet()).thenReturn(zset);
            lenient().when(appConfig.getOfflineMessageCount()).thenReturn(2);
            when(zset.zCard(anyString())).thenReturn(3L); // 3 > 2, exceeds limit

            // Mock the eviction: return an old entry when ranged
            Set<ZSetOperations.TypedTuple<String>> oldestSet = new HashSet<>();
            ZSetOperations.TypedTuple<String> oldest = mock(ZSetOperations.TypedTuple.class);
            when(oldest.getValue()).thenReturn("{\"messageKey\":1,\"fromId\":\"oldUser\",\"appId\":100}");
            lenient().when(oldest.getScore()).thenReturn(1.0);
            oldestSet.add(oldest);
            when(zset.rangeWithScores(anyString(), eq(0L), eq(0L))).thenReturn(oldestSet);

            service.storeOfflineMessage(msg);

            // Should evict (persist to DB + remove from ZSet)
            verify(imMessageHistoryMapper, atLeastOnce()).insert(any(ImMessageHistoryEntity.class));
            verify(zset, atLeast(1)).removeRange(anyString(), eq(0L), eq(0L));
        }

        private OfflineMessageContent createOfflineMsg() {
            OfflineMessageContent msg = new OfflineMessageContent();
            msg.setAppId(100);
            msg.setFromId("user001");
            msg.setToId("user002");
            msg.setMessageBody("hello");
            msg.setMessageKey(12345L);
            msg.setConversationType(ConversationTypeEnum.P2P.getCode());
            return msg;
        }
    }

    @Nested
    @DisplayName("storeGroupOfflineMessage - 群聊离线消息成员级隔离")
    class StoreGroupOfflineMessageTest {

        @Test
        @DisplayName("每个成员存储独立，一个成员失败不影响其他成员")
        void oneMemberFailureDoesNotAffectOthers() {
            OfflineMessageContent msg = createGroupOfflineMsg();
            List<String> memberIds = List.of("user002", "user003", "user004");

            ZSetOperations<String, String> zset = mock(ZSetOperations.class);
            when(stringRedisTemplate.opsForZSet()).thenReturn(zset);
            lenient().when(appConfig.getOfflineMessageCount()).thenReturn(500);
            when(zset.zCard(anyString())).thenReturn(10L);

            // user002: success
            // user003: Redis fails
            when(zset.add(contains("user003"), anyString(), anyDouble()))
                    .thenThrow(new RuntimeException("Redis fail for user003"));

            // Also for zCard: user003 ZSet gets Redis failure
            when(zset.zCard(contains("user003")))
                    .thenThrow(new RuntimeException("Redis fail for user003"));

            service.storeGroupOfflineMessage(msg, memberIds);

            // user002 & user004: should have been stored in ZSet (success)
            // user003: should have fallen back to DB
            verify(imMessageHistoryMapper, atLeast(1)).insert(any(ImMessageHistoryEntity.class));
        }

        private OfflineMessageContent createGroupOfflineMsg() {
            OfflineMessageContent msg = new OfflineMessageContent();
            msg.setAppId(100);
            msg.setToId("group001");
            msg.setFromId("user001");
            msg.setMessageBody("hello group");
            msg.setMessageKey(54321L);
            msg.setConversationType(ConversationTypeEnum.GROUP.getCode());
            return msg;
        }
    }

    @Nested
    @DisplayName("setMessageFromMessageIdCache - 缓存操作重试")
    class SetCacheTest {

        private ValueOperations<String, String> valueOps;

        @BeforeEach
        void setUp() {
            valueOps = mock(ValueOperations.class);
            when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
        }

        @Test
        @DisplayName("Redis 正常时直接设置缓存")
        void cacheSuccess() {
            service.setMessageFromMessageIdCache(100, "msg-001", "payload");

            verify(valueOps, times(1))
                    .set(anyString(), anyString(), anyLong(), any());
        }

        @Test
        @DisplayName("Redis 第一次失败后重试一次")
        void retryOnFirstFailure() {
            doThrow(new RuntimeException("Redis timeout"))
                    .doNothing()
                    .when(valueOps).set(anyString(), anyString(), anyLong(), any());

            service.setMessageFromMessageIdCache(100, "msg-001", "payload");

            verify(valueOps, times(2))
                    .set(anyString(), anyString(), anyLong(), any());
        }
    }
}
