package com.vela.im.service.message.domain.service;

import com.vela.im.service.friendship.application.dto.req.GetRelationReq;
import com.vela.im.service.friendship.domain.entity.ImFriendShipEntity;
import com.vela.im.service.friendship.domain.service.ImFriendService;
import com.vela.im.service.group.application.dto.resp.GetRoleInGroupResp;
import com.vela.im.service.group.domain.entity.ImGroupEntity;
import com.vela.im.service.group.domain.service.ImGroupMemberService;
import com.vela.im.service.group.domain.service.ImGroupService;
import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.types.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <p>Title: CheckSendMessageServiceTest</p>
 * <p>Description: CheckSendMessageService 单元测试，覆盖发送前校验的所有边界场景</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-24
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CheckSendMessageService - 消息发送前置校验")
class CheckSendMessageServiceTest {

    @Mock
    private ImUserService imUserService;
    @Mock
    private ImFriendService imFriendService;
    @Mock
    private ImGroupService imGroupService;
    @Mock
    private ImGroupMemberService imGroupMemberService;
    @Mock
    private AppConfig appConfig;

    private CheckSendMessageService service;

    @BeforeEach
    void setUp() {
        service = new CheckSendMessageService(imUserService, imFriendService,
                imGroupService, imGroupMemberService, appConfig);
    }

    @Nested
    @DisplayName("checkSenderForvidAndMute - 发送方禁言/禁用校验")
    class SenderForbidMuteTest {

        private final String FROM_ID = "user001";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("正常用户应返回成功")
        void normalUserReturnsOk() {
            ImUserDataEntity user = new ImUserDataEntity();
            user.setForbiddenFlag(UserForbiddenFlagEnum.NORMAL.getCode());
            user.setSilentFlag(UserSilentFlagEnum.NORMAL.getCode());
            when(imUserService.getSingleUserInfo(FROM_ID, APP_ID))
                    .thenReturn(Result.ok(user));

            Result result = service.checkSenderForvidAndMute(FROM_ID, APP_ID);

            assertTrue(result.isOk());
        }

        @Test
        @DisplayName("用户不存在应返回错误")
        void userNotFoundReturnsError() {
            when(imUserService.getSingleUserInfo(FROM_ID, APP_ID))
                    .thenReturn(Result.fail(UserErrorCode.USER_IS_NOT_EXIST));

            Result result = service.checkSenderForvidAndMute(FROM_ID, APP_ID);

            assertFalse(result.isOk());
        }

        @Test
        @DisplayName("被禁用的用户应返回禁禁用错误")
        void forbiddenUserReturnsError() {
            ImUserDataEntity user = new ImUserDataEntity();
            user.setForbiddenFlag(UserForbiddenFlagEnum.FORBIBBEN.getCode());
            user.setSilentFlag(UserSilentFlagEnum.NORMAL.getCode());
            when(imUserService.getSingleUserInfo(FROM_ID, APP_ID))
                    .thenReturn(Result.ok(user));

            Result result = service.checkSenderForvidAndMute(FROM_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(MessageErrorCode.FROMER_IS_FORBIBBEN.getCode(), result.getCode());
        }

        @Test
        @DisplayName("被禁言的用户应返回禁言错误")
        void mutedUserReturnsError() {
            ImUserDataEntity user = new ImUserDataEntity();
            user.setForbiddenFlag(UserForbiddenFlagEnum.NORMAL.getCode());
            user.setSilentFlag(UserSilentFlagEnum.MUTE.getCode());
            when(imUserService.getSingleUserInfo(FROM_ID, APP_ID))
                    .thenReturn(Result.ok(user));

            Result result = service.checkSenderForvidAndMute(FROM_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(MessageErrorCode.FROMER_IS_MUTE.getCode(), result.getCode());
        }
    }

    @Nested
    @DisplayName("checkFriendShip - 好友关系校验")
    class FriendshipTest {

        private final String FROM_ID = "user001";
        private final String TO_ID = "user002";
        private final Integer APP_ID = 100;

        @BeforeEach
        void setUp() {
            when(appConfig.isSendMessageCheckFriend()).thenReturn(true);
        }

        @Test
        @DisplayName("双方好友关系正常应返回成功")
        void bothFriendsNormalReturnsOk() {
            ImFriendShipEntity fromRelation = createFriendShip(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode(),
                    FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            ImFriendShipEntity toRelation = createFriendShip(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode(),
                    FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            when(imFriendService.getRelation(any(GetRelationReq.class)))
                    .thenReturn(Result.ok(fromRelation), Result.ok(toRelation));

            Result result = service.checkFriendShip(FROM_ID, TO_ID, APP_ID);

            assertTrue(result.isOk());
        }

        @Test
        @DisplayName("发送方不是接收方好友应返回错误")
        void senderNotFriendReturnsError() {
            ImFriendShipEntity fromRelation = createFriendShip(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode(),
                    FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            when(imFriendService.getRelation(any(GetRelationReq.class)))
                    .thenReturn(Result.ok(fromRelation));

            Result result = service.checkFriendShip(FROM_ID, TO_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(FriendShipErrorCode.FRIEND_IS_DELETED.getCode(), result.getCode());
        }

        @Test
        @DisplayName("接收方不是发送方好友应返回错误")
        void receiverNotFriendReturnsError() {
            ImFriendShipEntity fromRelation = createFriendShip(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode(),
                    FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            ImFriendShipEntity toRelation = createFriendShip(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode(),
                    FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            when(imFriendService.getRelation(any(GetRelationReq.class)))
                    .thenReturn(Result.ok(fromRelation), Result.ok(toRelation));

            Result result = service.checkFriendShip(FROM_ID, TO_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(FriendShipErrorCode.FRIEND_IS_DELETED.getCode(), result.getCode());
        }

        @Test
        @DisplayName("发送方在黑名单中应返回黑名单错误")
        void senderBlacklistedReturnsError() {
            when(appConfig.isSendMessageCheckBlack()).thenReturn(true);
            ImFriendShipEntity fromRelation = createFriendShip(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode(),
                    FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            ImFriendShipEntity toRelation = createFriendShip(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode(),
                    FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            when(imFriendService.getRelation(any(GetRelationReq.class)))
                    .thenReturn(Result.ok(fromRelation), Result.ok(toRelation));

            Result result = service.checkFriendShip(FROM_ID, TO_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(FriendShipErrorCode.TARGET_IS_BLACK_YOU.getCode(), result.getCode());
        }

        @Test
        @DisplayName("不开启好友关系检查时应直接返回成功")
        void friendCheckDisabledReturnsOk() {
            when(appConfig.isSendMessageCheckFriend()).thenReturn(false);

            Result result = service.checkFriendShip(FROM_ID, TO_ID, APP_ID);

            assertTrue(result.isOk());
            verify(imFriendService, never()).getRelation(any());
        }

        private ImFriendShipEntity createFriendShip(int status, int black) {
            ImFriendShipEntity entity = new ImFriendShipEntity();
            entity.setStatus(status);
            entity.setBlack(black);
            return entity;
        }
    }

    @Nested
    @DisplayName("checkGroupMessage - 群聊消息校验")
    class GroupMessageTest {

        private final String FROM_ID = "user001";
        private final String GROUP_ID = "group001";
        private final Integer APP_ID = 100;

        @Test
        @DisplayName("正常群成员发送消息应返回成功")
        void normalMemberReturnsOk() {
            setupNormalUser();
            setupExistingGroup(GroupMuteTypeEnum.NOT_MUTE.getCode());
            setupMemberRole(GroupMemberRoleEnum.MAMAGER.getCode(), null);

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertTrue(result.isOk());
        }

        @Test
        @DisplayName("发送方被禁用时应返回错误")
        void forbiddenUserReturnsError() {
            ImUserDataEntity user = new ImUserDataEntity();
            user.setForbiddenFlag(UserForbiddenFlagEnum.FORBIBBEN.getCode());
            user.setSilentFlag(UserSilentFlagEnum.NORMAL.getCode());
            when(imUserService.getSingleUserInfo(FROM_ID, APP_ID))
                    .thenReturn(Result.ok(user));

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(MessageErrorCode.FROMER_IS_FORBIBBEN.getCode(), result.getCode());
        }

        @Test
        @DisplayName("群组不存在应返回错误")
        void groupNotFoundReturnsError() {
            setupNormalUser();
            when(imGroupService.getGroup(GROUP_ID, APP_ID))
                    .thenReturn(Result.fail(GroupErrorCode.GROUP_IS_NOT_EXIST));

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(GroupErrorCode.GROUP_IS_NOT_EXIST.getCode(), result.getCode());
        }

        @Test
        @DisplayName("发送者不在群中应返回错误")
        void notGroupMemberReturnsError() {
            setupNormalUser();
            setupExistingGroup(GroupMuteTypeEnum.NOT_MUTE.getCode());
            when(imGroupMemberService.getRoleInGroupOne(GROUP_ID, FROM_ID, APP_ID))
                    .thenReturn(Result.fail(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP));

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertFalse(result.isOk());
        }

        @Test
        @DisplayName("全员禁言时普通成员发送应返回错误")
        void groupMutedForNormalMemberReturnsError() {
            setupNormalUser();
            setupExistingGroup(GroupMuteTypeEnum.MUTE.getCode());
            setupMemberRole(GroupMemberRoleEnum.MAMAGER.getCode(), null);

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(GroupErrorCode.THIS_GROUP_IS_MUTE.getCode(), result.getCode());
        }

        @Test
        @DisplayName("全员禁言时群主发送也应返回错误（代码逻辑如此）")
        void groupMutedButOwnerCanSend() {
            setupNormalUser();
            setupExistingGroup(GroupMuteTypeEnum.MUTE.getCode());
            setupMemberRole(GroupMemberRoleEnum.OWNER.getCode(), null);

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(GroupErrorCode.THIS_GROUP_IS_MUTE.getCode(), result.getCode());
        }

        @Test
        @DisplayName("成员被单独禁言时应返回错误")
        void memberMutedReturnsError() {
            setupNormalUser();
            setupExistingGroup(GroupMuteTypeEnum.NOT_MUTE.getCode());
            setupMemberRole(GroupMemberRoleEnum.MAMAGER.getCode(),
                    System.currentTimeMillis() + 60000); // muted for another 60s

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertFalse(result.isOk());
            assertEquals(GroupErrorCode.GROUP_MEMBER_IS_SPEAK.getCode(), result.getCode());
        }

        @Test
        @DisplayName("成员禁言已过期应返回成功")
        void memberMuteExpiredReturnsOk() {
            setupNormalUser();
            setupExistingGroup(GroupMuteTypeEnum.NOT_MUTE.getCode());
            setupMemberRole(GroupMemberRoleEnum.MAMAGER.getCode(),
                    System.currentTimeMillis() - 60000); // muted expired 60s ago

            Result result = service.checkGroupMessage(FROM_ID, GROUP_ID, APP_ID);

            assertTrue(result.isOk());
        }

        private void setupNormalUser() {
            ImUserDataEntity user = new ImUserDataEntity();
            user.setForbiddenFlag(UserForbiddenFlagEnum.NORMAL.getCode());
            user.setSilentFlag(UserSilentFlagEnum.NORMAL.getCode());
            when(imUserService.getSingleUserInfo(FROM_ID, APP_ID))
                    .thenReturn(Result.ok(user));
        }

        private void setupExistingGroup(int muteType) {
            ImGroupEntity group = new ImGroupEntity();
            group.setGroupId(GROUP_ID);
            group.setMute(muteType);
            when(imGroupService.getGroup(GROUP_ID, APP_ID))
                    .thenReturn(Result.ok(group));
        }

        private void setupMemberRole(int role, Long speakDate) {
            GetRoleInGroupResp resp = new GetRoleInGroupResp();
            resp.setRole(role);
            resp.setSpeakDate(speakDate);
            when(imGroupMemberService.getRoleInGroupOne(GROUP_ID, FROM_ID, APP_ID))
                    .thenReturn(Result.ok(resp));
        }
    }
}
