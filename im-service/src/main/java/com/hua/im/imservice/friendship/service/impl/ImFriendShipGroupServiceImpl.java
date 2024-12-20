package com.hua.im.imservice.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imcommon.enums.DelFlagEnum;
import com.hua.im.imservice.friendship.dao.ImFriendShipGroupEntity;
import com.hua.im.imservice.friendship.dao.mapper.ImFriendShipGroupMapper;
import com.hua.im.imservice.friendship.model.req.AddFriendShipGroupReq;
import com.hua.im.imservice.friendship.model.req.DeleteFriendShipGroupReq;
import com.hua.im.imservice.friendship.service.ImFriendService;
import com.hua.im.imservice.friendship.service.ImFriendShipGroupMemberService;
import com.hua.im.imservice.friendship.service.ImFriendShipGroupService;
import org.springframework.beans.factory.annotation.Autowired;
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
        queryWrapper.eq("group_name",req.getGroupName());
        queryWrapper.eq("app_id",req.getAppId());
        queryWrapper.eq("from_id",req.getFromId());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());




        return null;
    }

    @Override
    public ResponseVO deleteGroup(DeleteFriendShipGroupReq req) {
        return null;
    }

    @Override
    public ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId) {
        return null;
    }
}
