package com.hua.im.imservice.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imservice.friendship.dao.ImFriendShipGroupEntity;
import com.hua.im.imservice.friendship.dao.ImFriendShipGroupMemberEntity;
import com.hua.im.imservice.friendship.dao.mapper.ImFriendShipGroupMemberMapper;
import com.hua.im.imservice.friendship.model.req.AddFriendShipGroupMemberReq;
import com.hua.im.imservice.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.hua.im.imservice.friendship.service.ImFriendShipGroupMemberService;
import com.hua.im.imservice.friendship.service.ImFriendShipGroupService;
import com.hua.im.imservice.user.dao.ImUserDataEntity;
import com.hua.im.imservice.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shukun.Li
 */
public class ImFriendShipGroupMemberServiceImpl implements ImFriendShipGroupMemberService {

    @Autowired
    ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;

    @Autowired
    ImFriendShipGroupService imFriendShipGroupService;

    @Autowired
    ImUserService imUserService;

    @Autowired
    ImFriendShipGroupMemberService thisService;

    @Override
    @Transactional
    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req) {

        ResponseVO<ImFriendShipGroupEntity> imFriendShipGroupEntityResponseVO = imFriendShipGroupService
                .getGroup(req.getFromId(), req.getGroupName(), req.getAppId());

        if (!imFriendShipGroupEntityResponseVO.isOk()) {
            return imFriendShipGroupEntityResponseVO;
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if (singleUserInfo.isOk()) {
                int i = thisService.doAddGroupMember(imFriendShipGroupEntityResponseVO.getData().getGroupId(),
                        toId);
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }

        return ResponseVO.successResponse(successId);
    }

    @Override
    public ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService
                .getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }

        ArrayList<String> list = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if (singleUserInfo.isOk()) {
                int i = deleteGroupMember(group.getData().getGroupId(), toId);
                if (i == 1) {
                    list.add(toId);
                }
            }
        }
        return ResponseVO.successResponse(list);
    }

    @Override
    public int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);
        try {
            int insert = imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
            return insert;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        return imFriendShipGroupMemberMapper.delete(queryWrapper);
    }


    public int deleteGroupMember(Long groupId, String toId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        queryWrapper.eq("to_id", toId);

        try {
            return imFriendShipGroupMemberMapper.delete(queryWrapper);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
