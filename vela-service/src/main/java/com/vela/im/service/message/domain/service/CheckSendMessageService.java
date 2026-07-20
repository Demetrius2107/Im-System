package com.vela.im.service.message.domain.service;


import com.vela.im.service.friendship.domain.entity.ImFriendShipEntity;
import com.vela.im.service.friendship.application.dto.req.GetRelationReq;
import com.vela.im.service.friendship.domain.service.ImFriendService;
import com.vela.im.service.group.domain.entity.ImGroupEntity;
import com.vela.im.service.group.application.dto.resp.GetRoleInGroupResp;
import com.vela.im.service.group.domain.service.ImGroupMemberService;
import com.vela.im.service.group.domain.service.ImGroupService;
import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.types.enums.*;
import org.springframework.stereotype.Service;

/**
 * <p>Title: CheckSendMessageService</p>
 * <p>Description: 消息发送前置校验服务，校验发送方禁言/禁用状态、好友关系、群组权限等。</p>
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
public class CheckSendMessageService {

    private final ImUserService imUserService;
    private final ImFriendService imFriendService;
    private final ImGroupService imGroupService;
    private final ImGroupMemberService imGroupMemberService;
    private final AppConfig appConfig;

    public CheckSendMessageService(ImUserService imUserService,
                                   ImFriendService imFriendService,
                                   ImGroupService imGroupService,
                                   ImGroupMemberService imGroupMemberService,
                                   AppConfig appConfig) {
        this.imUserService = imUserService;
        this.imFriendService = imFriendService;
        this.imGroupService = imGroupService;
        this.imGroupMemberService = imGroupMemberService;
        this.appConfig = appConfig;
    }

    /**
     * Check if sender is muted or banned.
     *
     * @param fromId sender user ID
     * @param appId  application ID
     * @return success if allowed, error code if muted/banned
     */
    public ResponseVO checkSenderForvidAndMute(String fromId, Integer appId){

        ResponseVO<ImUserDataEntity> singleUserInfo
                = imUserService.getSingleUserInfo(fromId, appId);
        if(!singleUserInfo.isOk()){
            return singleUserInfo;
        }

        ImUserDataEntity user = singleUserInfo.getData();
        if(user.getForbiddenFlag() == UserForbiddenFlagEnum.FORBIBBEN.getCode()){
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_FORBIBBEN);
        }else if (user.getSilentFlag() == UserSilentFlagEnum.MUTE.getCode()){
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_MUTE);
        }

        return ResponseVO.successResponse();
    }

    /**
     * Check friendship between sender and receiver.
     * <p>Validates friend status and blacklist, depending on configuration.</p>
     *
     * @param fromId sender user ID
     * @param toId   receiver user ID
     * @param appId  application ID
     * @return success if allowed, error code if not friends / blacklisted
     */
    public ResponseVO checkFriendShip(String fromId, String toId, Integer appId){

        if(appConfig.isSendMessageCheckFriend()){
            // Check from → to friendship
            GetRelationReq fromReq = buildRelationReq(fromId, toId, appId);
            ResponseVO<ImFriendShipEntity> fromRelation = imFriendService.getRelation(fromReq);
            if(!fromRelation.isOk()){
                return fromRelation;
            }

            // Check to → from friendship (reverse direction)
            GetRelationReq toReq = buildRelationReq(toId, fromId, appId);
            ResponseVO<ImFriendShipEntity> toRelation = imFriendService.getRelation(toReq);
            if(!toRelation.isOk()){
                return toRelation;
            }

            // Validate friend status (both directions must be normal)
            ResponseVO check = checkFriendStatus(fromRelation.getData(), toRelation.getData());
            if(!check.isOk()){
                return check;
            }
        }

        return ResponseVO.successResponse();
    }

    /**
     * Build a GetRelationReq for the given user pair.
     */
    private GetRelationReq buildRelationReq(String fromId, String toId, Integer appId) {
        GetRelationReq req = new GetRelationReq();
        req.setFromId(fromId);
        req.setToId(toId);
        req.setAppId(appId);
        return req;
    }

    /**
     * Validate friend status and blacklist for both directions.
     */
    private ResponseVO checkFriendStatus(ImFriendShipEntity fromRelation, ImFriendShipEntity toRelation) {
        if(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != fromRelation.getStatus()){
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
        }

        if(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != toRelation.getStatus()){
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
        }

        if(appConfig.isSendMessageCheckBlack()){
            if(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != fromRelation.getBlack()){
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
            }

            if(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != toRelation.getBlack()){
                return ResponseVO.errorResponse(FriendShipErrorCode.TARGET_IS_BLACK_YOU);
            }
        }

        return ResponseVO.successResponse();
    }

    /**
     * Check group message sending permissions.
     * <p>Validates: sender mute/ban → group exists → member in group → group mute (admin/owner exempt) → member mute.</p>
     *
     * @param fromId  sender user ID
     * @param groupId group ID
     * @param appId   application ID
     * @return success if allowed, error code otherwise
     */
    public ResponseVO checkGroupMessage(String fromId, String groupId, Integer appId){

        ResponseVO responseVO = checkSenderForvidAndMute(fromId, appId);
        if(!responseVO.isOk()){
            return responseVO;
        }

        // Check if group exists
        ResponseVO<ImGroupEntity> group = imGroupService.getGroup(groupId, appId);
        if(!group.isOk()){
            return group;
        }

        // Check if sender is a group member
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = imGroupMemberService.getRoleInGroupOne(groupId, fromId, appId);
        if(!roleInGroupOne.isOk()){
            return roleInGroupOne;
        }
        GetRoleInGroupResp data = roleInGroupOne.getData();

        // Check group-wide mute: only admin/owner can speak when muted
        ImGroupEntity groupData = group.getData();
        if(groupData.getMute() == GroupMuteTypeEnum.MUTE.getCode()
         && (data.getRole() == GroupMemberRoleEnum.MAMAGER.getCode() ||
                data.getRole() == GroupMemberRoleEnum.OWNER.getCode()  )){
            return ResponseVO.errorResponse(GroupErrorCode.THIS_GROUP_IS_MUTE);
        }

        // Check individual member mute
        if(data.getSpeakDate() != null && data.getSpeakDate() > System.currentTimeMillis()){
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_MEMBER_IS_SPEAK);
        }

        return ResponseVO.successResponse();
    }


}
