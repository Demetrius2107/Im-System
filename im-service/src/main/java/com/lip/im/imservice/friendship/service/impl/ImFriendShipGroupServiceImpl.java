package com.lip.im.imservice.friendship.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imcommon.enums.DelFlagEnum;
import com.hua.im.imcommon.enums.FriendShipErrorCode;
import com.lip.im.imservice.friendship.dao.ImFriendShipGroupEntity;
import com.lip.im.imservice.friendship.dao.mapper.ImFriendShipGroupMapper;
import com.lip.im.imservice.friendship.model.req.AddFriendShipGroupMemberReq;
import com.lip.im.imservice.friendship.model.req.AddFriendShipGroupReq;
import com.lip.im.imservice.friendship.model.req.DeleteFriendShipGroupReq;
import com.lip.im.imservice.friendship.service.ImFriendService;
import com.lip.im.imservice.friendship.service.ImFriendShipGroupMemberService;
import com.lip.im.imservice.friendship.service.ImFriendShipGroupService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Shukun.Li
 */
@Service
public class ImFriendShipGroupServiceImpl implements ImFriendShipGroupService {

    @Autowired
    ImFriendService imFriendService;

    @Autowired
    ImFriendShipGroupMapper imFriendShipGroupMapper;

    @Autowired
    ImFriendShipGroupMemberService imFriendShipGroupMemberService;


    @Override
    @Transactional
    public ResponseVO addGroup(AddFriendShipGroupReq req) {

        QueryWrapper<ImFriendShipGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_name", req.getGroupName());
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.eq("from_id", req.getFromId());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());


        ImFriendShipGroupEntity imFriendShipGroupEntity = imFriendShipGroupMapper.selectOne(queryWrapper);
        if (imFriendShipGroupEntity != null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        // 写入DB
        ImFriendShipGroupEntity insertImFriendShipGroupEntity = new ImFriendShipGroupEntity();
        insertImFriendShipGroupEntity.setAppId(req.getAppId());
        insertImFriendShipGroupEntity.setCreateTime(System.currentTimeMillis());
        insertImFriendShipGroupEntity.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insertImFriendShipGroupEntity.setGroupName(req.getGroupName());
        insertImFriendShipGroupEntity.setFromId(req.getFromId());
        try {
            int insertImFriendShipGroupResult = imFriendShipGroupMapper.insert(insertImFriendShipGroupEntity);

            if (insertImFriendShipGroupResult != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }

            if (CollectionUtil.isNotEmpty(req.getToIds()) && ObjectUtils.isNotEmpty(insertImFriendShipGroupResult)) {
                AddFriendShipGroupMemberReq addFriendShipGroupMemberReq = new AddFriendShipGroupMemberReq();
                addFriendShipGroupMemberReq.setFromId(req.getFromId());
                addFriendShipGroupMemberReq.setGroupName(req.getGroupName());
                addFriendShipGroupMemberReq.setAppId(req.getAppId());
                imFriendShipGroupMemberService.addGroupMember(addFriendShipGroupMemberReq);
                return ResponseVO.successResponse();
            }
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
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

            ImFriendShipGroupEntity imFriendShipGroupEntity = imFriendShipGroupMapper.selectOne(query);

            if (imFriendShipGroupEntity != null) {
                ImFriendShipGroupEntity updateImFriendShipGroupEntity = new ImFriendShipGroupEntity();
                updateImFriendShipGroupEntity.setGroupId(imFriendShipGroupEntity.getGroupId());
                updateImFriendShipGroupEntity.setDelFlag(DelFlagEnum.DELETE.getCode());
                imFriendShipGroupMapper.updateById(updateImFriendShipGroupEntity);
                imFriendShipGroupMemberService.clearGroupMember(imFriendShipGroupEntity.getGroupId());
            }

        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_name", groupName);
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("from_id", fromId);

        ImFriendShipGroupEntity imFriendShipGroupEntity = imFriendShipGroupMapper.selectOne(queryWrapper);
        if (imFriendShipGroupEntity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        return ResponseVO.successResponse();
    }
}
