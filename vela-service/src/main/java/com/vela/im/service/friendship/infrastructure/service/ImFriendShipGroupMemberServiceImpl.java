package com.vela.im.service.friendship.infrastructure.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.vela.im.service.friendship.domain.entity.ImFriendShipGroupEntity;
import com.vela.im.service.friendship.domain.entity.ImFriendShipGroupMemberEntity;
import com.vela.im.service.friendship.infrastructure.persistence.mapper.ImFriendShipGroupMemberMapper;
import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupMemberReq;
import com.vela.im.service.friendship.application.dto.req.DeleteFriendShipGroupMemberReq;
import com.vela.im.service.friendship.domain.service.ImFriendShipGroupMemberService;
import com.vela.im.service.friendship.domain.service.ImFriendShipGroupService;
import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.types.enums.command.FriendshipEventCommand;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.codec.pack.friendship.AddFriendGroupMemberPack;
import com.vela.im.codec.pack.friendship.DeleteFriendGroupMemberPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: ImFriendShipGroupMemberServiceImpl</p>
 * <p>Description: 好友分组成员管理实现，处理分组内成员的添加和删除。</p>
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
public class ImFriendShipGroupMemberServiceImpl
        implements ImFriendShipGroupMemberService {

    private final ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;
    private final ImFriendShipGroupService imFriendShipGroupService;
    private final ImUserService imUserService;
    private final ImFriendShipGroupMemberService thisService;
    private final MessageProducer messageProducer;

    public ImFriendShipGroupMemberServiceImpl(ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper,
                                              ImFriendShipGroupService imFriendShipGroupService,
                                              ImUserService imUserService,
                                              ImFriendShipGroupMemberService thisService,
                                              MessageProducer messageProducer) {
        this.imFriendShipGroupMemberMapper = imFriendShipGroupMemberMapper;
        this.imFriendShipGroupService = imFriendShipGroupService;
        this.imUserService = imUserService;
        this.thisService = thisService;
        this.messageProducer = messageProducer;
    }

    @Override
    @Transactional
    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req) {

        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService
                .getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                int i = thisService.doAddGroupMember(group.getData().getGroupId(), toId);
                if(i == 1){
                    successId.add(toId);
                }
            }
        }

        Long seq = imFriendShipGroupService.updateSeq(req.getFromId(), req.getGroupName(), req.getAppId());
        AddFriendGroupMemberPack pack = new AddFriendGroupMemberPack();
        pack.setFromId(req.getFromId());
        pack.setGroupName(req.getGroupName());
        pack.setToIds(successId);
        pack.setSequence(seq);
        messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_MEMBER_ADD,
                pack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));

        return ResponseVO.successResponse(successId);
    }

    @Override
    public ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService
                .getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                int i = deleteGroupMember(group.getData().getGroupId(), toId);
                if(i == 1){
                    successId.add(toId);
                }
            }
        }

        Long seq = imFriendShipGroupService.updateSeq(req.getFromId(), req.getGroupName(), req.getAppId());
        DeleteFriendGroupMemberPack pack = new DeleteFriendGroupMemberPack();
        pack.setFromId(req.getFromId());
        pack.setGroupName(req.getGroupName());
        pack.setToIds(successId);
        pack.setSequence(seq);
        messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_MEMBER_DELETE,
                pack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));
        return ResponseVO.successResponse(successId);
    }

    @Override
    public int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);

        try {
            int insert = imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
            return insert;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int deleteGroupMember(Long groupId, String toId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id",groupId);
        queryWrapper.eq("to_id",toId);

        try {
            int delete = imFriendShipGroupMemberMapper.delete(queryWrapper);
//            int insert = imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
            return delete;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id",groupId);
        int delete = imFriendShipGroupMemberMapper.delete(query);
        return delete;
    }
}
