package com.vela.im.service.friendship.infrastructure.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.vela.im.service.friendship.domain.entity.ImFriendShipGroupEntity;
import com.vela.im.service.friendship.infrastructure.persistence.mapper.ImFriendShipGroupMapper;
import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupMemberReq;
import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupReq;
import com.vela.im.service.friendship.application.dto.req.DeleteFriendShipGroupReq;
import com.vela.im.service.friendship.domain.service.ImFriendShipGroupMemberService;
import com.vela.im.service.friendship.domain.service.ImFriendShipGroupService;
import com.vela.im.service.infrastructure.seq.RedisSeq;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.service.application.utils.WriteUserSeq;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.DelFlagEnum;
import com.vela.im.shared.types.enums.FriendShipErrorCode;
import com.vela.im.shared.types.enums.command.FriendshipEventCommand;
import com.vela.im.shared.types.ClientInfo;
import com.vela.im.codec.pack.friendship.AddFriendGroupPack;
import com.vela.im.codec.pack.friendship.DeleteFriendGroupPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>Title: ImFriendShipGroupServiceImpl</p>
 * <p>Description: 好友分组管理实现，处理好友分组的创建、删除、查询及序列更新。</p>
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
public class ImFriendShipGroupServiceImpl implements ImFriendShipGroupService {

    private final ImFriendShipGroupMapper imFriendShipGroupMapper;
    private final ImFriendShipGroupMemberService imFriendShipGroupMemberService;
    private final ImUserService imUserService;
    private final RedisSeq redisSeq;
    private final MessageProducer messageProducer;
    private final WriteUserSeq writeUserSeq;

    public ImFriendShipGroupServiceImpl(ImFriendShipGroupMapper imFriendShipGroupMapper,
                                        ImFriendShipGroupMemberService imFriendShipGroupMemberService,
                                        ImUserService imUserService,
                                        RedisSeq redisSeq,
                                        MessageProducer messageProducer,
                                        WriteUserSeq writeUserSeq) {
        this.imFriendShipGroupMapper = imFriendShipGroupMapper;
        this.imFriendShipGroupMemberService = imFriendShipGroupMemberService;
        this.imUserService = imUserService;
        this.redisSeq = redisSeq;
        this.messageProducer = messageProducer;
        this.writeUserSeq = writeUserSeq;
    }

    @Override
    @Transactional
    public ResponseVO addGroup(AddFriendShipGroupReq req) {

        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", req.getGroupName());
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

        if (entity != null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        //写入db
        ImFriendShipGroupEntity insert = new ImFriendShipGroupEntity();
        insert.setAppId(req.getAppId());
        insert.setCreateTime(System.currentTimeMillis());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insert.setGroupName(req.getGroupName());
        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.FriendshipGroup);
        insert.setSequence(seq);
        insert.setFromId(req.getFromId());
        try {
            int insert1 = imFriendShipGroupMapper.insert(insert);

            if (insert1 != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }
            if (insert1 == 1 && CollectionUtil.isNotEmpty(req.getToIds())) {
                AddFriendShipGroupMemberReq addFriendShipGroupMemberReq = new AddFriendShipGroupMemberReq();
                addFriendShipGroupMemberReq.setFromId(req.getFromId());
                addFriendShipGroupMemberReq.setGroupName(req.getGroupName());
                addFriendShipGroupMemberReq.setToIds(req.getToIds());
                addFriendShipGroupMemberReq.setAppId(req.getAppId());
                imFriendShipGroupMemberService.addGroupMember(addFriendShipGroupMemberReq);
                return ResponseVO.successResponse();
            }
        } catch (DuplicateKeyException e) {
            e.getStackTrace();
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        AddFriendGroupPack addFriendGropPack = new AddFriendGroupPack();
        addFriendGropPack.setFromId(req.getFromId());
        addFriendGropPack.setGroupName(req.getGroupName());
        addFriendGropPack.setSequence(seq);
        messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_ADD,
                addFriendGropPack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));
        //写入seq
        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FriendshipGroup, seq);

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO deleteGroup(DeleteFriendShipGroupReq req) {

        for (String groupName : req.getGroupName()) {
            QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
            query.eq("group_name", groupName);
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getFromId());
            query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

            ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

            if (entity != null) {
                long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.FriendshipGroup);
                ImFriendShipGroupEntity update = new ImFriendShipGroupEntity();
                update.setSequence(seq);
                update.setGroupId(entity.getGroupId());
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                imFriendShipGroupMapper.updateById(update);
                imFriendShipGroupMemberService.clearGroupMember(entity.getGroupId());
                DeleteFriendGroupPack deleteFriendGroupPack = new DeleteFriendGroupPack();
                deleteFriendGroupPack.setFromId(req.getFromId());
                deleteFriendGroupPack.setGroupName(groupName);
                deleteFriendGroupPack.setSequence(seq);
                //TCP通知
                messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_DELETE,
                        deleteFriendGroupPack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));
                //写入seq
                writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FriendshipGroup, seq);
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getGroup(String fromId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    @Override
    public Long updateSeq(String fromId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", fromId);

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

        long seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.FriendshipGroup);

        ImFriendShipGroupEntity group = new ImFriendShipGroupEntity();
        group.setGroupId(entity.getGroupId());
        group.setSequence(seq);
        imFriendShipGroupMapper.updateById(group);
        return seq;
    }

}
