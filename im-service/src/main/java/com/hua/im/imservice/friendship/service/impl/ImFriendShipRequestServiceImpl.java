package com.hua.im.imservice.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imcommon.enums.ApproverFriendRequestStatusEnum;
import com.hua.im.imcommon.enums.FriendShipErrorCode;
import com.hua.im.imcommon.exception.ApplicationException;
import com.hua.im.imservice.friendship.dao.ImFriendShipRequestEntity;
import com.hua.im.imservice.friendship.dao.mapper.ImFriendShipRequestMapper;
import com.hua.im.imservice.friendship.model.req.ApproveFriendRequestReq;
import com.hua.im.imservice.friendship.model.req.FriendDto;
import com.hua.im.imservice.friendship.model.req.ReadFriendShipRequestReq;
import com.hua.im.imservice.friendship.service.ImFriendService;
import com.hua.im.imservice.friendship.service.ImFriendShipRequestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Shukun.Li
 */
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
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("from_id", fromId);
        queryWrapper.eq("to_id", dto.getToId());
        ImFriendShipRequestEntity request = imFriendShipRequestMapper.selectOne(queryWrapper);

        if (request == null) {
            request = new ImFriendShipRequestEntity();
            request.setAddSource(dto.getAddSource());
            request.setAddWording(dto.getAddWording());
            request.setAppId(appId);
            request.setFromId(fromId);
            request.setToId(dto.getToId());
            request.setReadStatus(0);
            request.setApproveStatus(0);
            request.setRemark(dto.getRemark());
            request.setCreateTime(System.currentTimeMillis());
            imFriendShipRequestMapper.insert(request);
        } else {

            // 修改记录内容和更新内容
            if (StringUtils.isNotBlank(dto.getAddSource())) {
                request.setAddWording(dto.getAddWording());
            }

            if (StringUtils.isNotBlank(dto.getRemark())) {
                request.setRemark(dto.getRemark());
            }

            if (StringUtils.isNotBlank(dto.getAddWording())) {
                request.setAddWording(dto.getAddWording());
            }

            request.setApproveStatus(0);
            request.setReadStatus(0);
            imFriendShipRequestMapper.updateById(request);

        }
        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO approveFriendRequest(ApproveFriendRequestReq req) {

        QueryWrapper<ImFriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        ImFriendShipRequestEntity imFriendShipRequestEntity = imFriendShipRequestMapper.selectById(req.getId());
        if (imFriendShipRequestEntity == null) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if (!req.getOperator().equals(imFriendShipRequestEntity.getToId())) {
            // 只能审批发给自己的好友请求
            throw new ApplicationException(FriendShipErrorCode.NOT_APPROVE_OTHER_MAN_REQUEST);
        }

        ImFriendShipRequestEntity updateImFriendShipRequestEntity = new ImFriendShipRequestEntity();
        updateImFriendShipRequestEntity.setApproveStatus(req.getStatus());
        updateImFriendShipRequestEntity.setUpdateTime(System.currentTimeMillis());
        updateImFriendShipRequestEntity.setId(req.getId());
        imFriendShipRequestMapper.updateById(updateImFriendShipRequestEntity);

        if (ApproverFriendRequestStatusEnum.AGREE.getCode() == req.getStatus()) {

            FriendDto dto = new FriendDto();
            dto.setAddSource(imFriendShipRequestEntity.getAddSource());
            dto.setAddWording(imFriendShipRequestEntity.getAddWording());
            dto.setRemark(imFriendShipRequestEntity.getRemark());
            dto.setToId(imFriendShipRequestEntity.getToId());
            ResponseVO responseVO = imFriendService
                    .doAddFriend(req, imFriendShipRequestEntity.getFromId(), dto, req.getAppId());

            if (!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCode.TO_IS_YOUR_FRIEND.getCode()) {
                return responseVO;
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req) {
        QueryWrapper<ImFriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",req.getAppId());
        queryWrapper.eq("to_id",req.getFromId());

        ImFriendShipRequestEntity updateImFriendShipRequestEntity = new ImFriendShipRequestEntity();
        updateImFriendShipRequestEntity.setReadStatus(1);
        imFriendShipRequestMapper.update(updateImFriendShipRequestEntity,queryWrapper);


        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getFriendRequest(String fromId, Integer appId) {
        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("to_id", fromId);

        List<ImFriendShipRequestEntity> requestList = imFriendShipRequestMapper.selectList(query);
        return ResponseVO.successResponse(requestList);
    }
}
