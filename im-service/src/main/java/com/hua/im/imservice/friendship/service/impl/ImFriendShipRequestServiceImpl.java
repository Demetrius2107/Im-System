package com.hua.im.imservice.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imservice.friendship.dao.ImFriendShipEntity;
import com.hua.im.imservice.friendship.dao.ImFriendShipRequestEntity;
import com.hua.im.imservice.friendship.dao.mapper.ImFriendShipRequestMapper;
import com.hua.im.imservice.friendship.model.req.ApproveFriendRequestReq;
import com.hua.im.imservice.friendship.model.req.FriendDto;
import com.hua.im.imservice.friendship.model.req.ReadFriendShipRequestReq;
import com.hua.im.imservice.friendship.service.ImFriendService;
import com.hua.im.imservice.friendship.service.ImFriendShipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImFriendShipRequestServiceImpl implements ImFriendShipRequestService {

    @Autowired
    ImFriendShipRequestMapper imFriendShipRequestMapper;

    @Autowired
    ImFriendService imFriendService;

    // A+B
    @Override
    public ResponseVO addFriendShipRequest(String fromId, FriendDto dto, Integer appId) {
        QueryWrapper<ImFriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",appId);
        queryWrapper.eq("from_id",fromId);
        queryWrapper.eq("to_id",dto.getToId());
        ImFriendShipRequestEntity request = imFriendShipRequestMapper.selectOne(queryWrapper);

        if(request == null){
            request = new ImFriendShipRequestEntity();
            request.setAddSource(dto.getAddSource());
           // request.setAddWording
        }

    }


    @Override
    public ResponseVO approveFriendRequest(ApproveFriendRequestReq req) {

        QueryWrapper<ImFriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("app_id",);
        return null;
    }

    @Override
    public ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req) {
        return null;
    }

    @Override
    public ResponseVO getFriendRequest(String fromId, Integer appId) {
        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq("app_id",appId);
        query.eq("to_id",fromId);

        List<ImFriendShipRequestEntity> requestList  = imFriendShipRequestMapper.selectList(query);
        return ResponseVO.successResponse(requestList);
    }
}
