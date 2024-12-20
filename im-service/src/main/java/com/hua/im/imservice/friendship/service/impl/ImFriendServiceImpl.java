package com.hua.im.imservice.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imcommon.enums.AllowFriendTypeEnum;
import com.hua.im.imcommon.enums.FriendShipErrorCode;
import com.hua.im.imcommon.enums.FriendShipStatusEnum;
import com.hua.im.imcommon.exception.ApplicationException;
import com.hua.im.imcommon.model.RequestBase;
import com.hua.im.imservice.friendship.dao.ImFriendShipEntity;
import com.hua.im.imservice.friendship.dao.mapper.ImFriendShipMapper;
import com.hua.im.imservice.friendship.model.req.*;
import com.hua.im.imservice.friendship.model.resp.ImportFriendShipResp;
import com.hua.im.imservice.friendship.service.ImFriendService;
import com.hua.im.imservice.user.dao.ImUserDataEntity;
import com.hua.im.imservice.user.service.ImUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shukun.Li
 */
@Service
public class ImFriendServiceImpl implements ImFriendService {

    @Autowired
    ImUserService imUserService;

    @Autowired
    ImFriendService imFriendService;

    @Autowired
    ImFriendShipMapper imFriendShipMapper;

    @Override
    public ResponseVO importFriendShip(ImportFriendShipReq req) {

        if (req.getFriendItem().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }
        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImportFriendShipReq.ImportFriendDto dto : req.getFriendItem()) {
            ImFriendShipEntity entity = new ImFriendShipEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            try {
                int insert = imFriendShipMapper.insert(entity);
                if (insert == 1) {
                    successId.add(dto.getToId());
                } else {
                    errorId.add(dto.getToId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(dto.getToId());
            }
        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO addFriend(AddFriendReq req) {
        // 查询A 添加好友发送方 用户对象
        ResponseVO<ImUserDataEntity> fromImUserInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (fromImUserInfo.isOk()) {
            return fromImUserInfo;
        }

        // 查询B 添加好友接收方 用户对象
        ResponseVO<ImUserDataEntity> toImUserInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (toImUserInfo.isOk()) {
            return toImUserInfo;
        }

        ImUserDataEntity data = toImUserInfo.getData();

        // 如果添加好友接收方B 好友添加类型不为空且验证类型为不需要验证 则直接执行添加好友逻辑
        if (data.getFriendAllowType() != null && data.getFriendAllowType() == AllowFriendTypeEnum.NOT_NEED.getCode()) {
            return this.doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
        } else {

            // 添加好友接收方需要验证
            LambdaQueryWrapper<ImFriendShipEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ImFriendShipEntity>()
                    .eq(ImFriendShipEntity::getAppId, req.getAppId())
                    .eq(ImFriendShipEntity::getFromId, req.getFromId())
                    .eq(ImFriendShipEntity::getToId, req.getToItem().getToId());

            ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(lambdaQueryWrapper);
            if (fromItem == null || fromItem.getStatus() != FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                //插入一条好友申请的数据
              /*  ResponseVO responseVO = imFriendShipRequestService.addFriendshipRequest(req.getFromId(), req.getToItem(), req.getAppId());
                if(!responseVO.isOk()){
                    return responseVO;
                }*/
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO updateFriend(UpdateFriendReq req) {
        // 更新好友
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }

        return this.doUpdate(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Transactional
    public ResponseVO doUpdate(String fromId, FriendDto dto, Integer appId) {
        LambdaQueryWrapper<ImFriendShipEntity> updateLambdaQueryWrapper = new LambdaQueryWrapper<ImFriendShipEntity>()
                .eq(ImFriendShipEntity::getExtra, dto.getExtra())
                .eq(ImFriendShipEntity::getExtra, dto.getRemark())
                .eq(ImFriendShipEntity::getAppId, appId)
                .eq(ImFriendShipEntity::getToId, dto.getToId())
                .eq(ImFriendShipEntity::getFromId, fromId);
        int update = imFriendShipMapper.update(null, updateLambdaQueryWrapper);
        if (update == 1) {
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse();
    }

    @Override
    public ResponseVO deleteFriend(DeleteFriendReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ImFriendShipEntity>()
                .eq(ImFriendShipEntity::getAppId, req.getAppId())
                .eq(ImFriendShipEntity::getFromId, req.getFromId())
                .eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(lambdaQueryWrapper);
        if (fromItem == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        } else {
            if (fromItem.getStatus() != null && fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                ImFriendShipEntity updateImFriendShip = new ImFriendShipEntity();
                updateImFriendShip.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
                imFriendShipMapper.update(updateImFriendShip, lambdaQueryWrapper);
            } else {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteAllFriend(DeleteFriendReq req) {
        return null;
    }

    @Override
    public ResponseVO getAllFriendShip(GetAllFriendShipReq req) {
        return null;
    }

    @Override
    public ResponseVO getRelation(GetRelationReq req) {
        return null;
    }

    @Override
    public ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId) {
        // A-B
        // Friend 表插入A和B两条记录(好友双方相互记录 to from)
        // 查询是否有记录存在，如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态

        // A记录逻辑
        // 查询好友关系链表中是否存在满足条件的数据
        LambdaQueryWrapper<ImFriendShipEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ImFriendShipEntity>()
                .eq(ImFriendShipEntity::getAppId, appId)
                .eq(ImFriendShipEntity::getFromId, fromId)
                .eq(ImFriendShipEntity::getToId, dto.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(lambdaQueryWrapper);

        // 如果不存在则执行添加逻辑
        if (fromItem == null) {
            // 添加逻辑,重新构建关系链实体
            fromItem = new ImFriendShipEntity();
            // 设置appId
            fromItem.setAppId(appId);
            // 添加好友申请方-->发送方ID
            fromItem.setFromId(fromId);

            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            // 关系链表新增
            int insertImFriendShip = imFriendShipMapper.insert(fromItem);
            if (insertImFriendShip != 1) {
                // 关系链表新增失败-->添加好友失败
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 如果存在则判断状态，如果是已添加，则提示已添加，流程数据
            // 如果是未添加，则修改状态
            if (fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            } else {
                // 构建新的好友关系链实体进行更新好友关系链状态
                ImFriendShipEntity updateImFriendShipEntity = new ImFriendShipEntity();

                if (StringUtils.isNotBlank(dto.getAddSource())) {
                    updateImFriendShipEntity.setAddSource(dto.getAddSource());
                }

                if (StringUtils.isNotBlank(dto.getRemark())) {
                    updateImFriendShipEntity.setRemark(dto.getRemark());
                }

                if (StringUtils.isNotBlank(dto.getExtra())) {
                    updateImFriendShipEntity.setExtra(dto.getExtra());
                }
                updateImFriendShipEntity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int result = imFriendShipMapper.update(updateImFriendShipEntity, lambdaQueryWrapper);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }

        }

        // B记录逻辑
        LambdaQueryWrapper<ImFriendShipEntity> toQuery = new LambdaQueryWrapper<ImFriendShipEntity>()
                .eq(ImFriendShipEntity::getAppId, appId)
                // 因为此条是B记录 所以接收方和发送方是相反的
                .eq(ImFriendShipEntity::getFromId, dto.getToId())
                .eq(ImFriendShipEntity::getToId, fromId);
        ImFriendShipEntity toItemFriendShipEntity = imFriendShipMapper.selectOne(toQuery);
        if (toItemFriendShipEntity == null) {
            toItemFriendShipEntity = new ImFriendShipEntity();
            toItemFriendShipEntity.setAppId(appId);
            toItemFriendShipEntity.setFromId(dto.getToId());
            BeanUtils.copyProperties(dto, toItemFriendShipEntity);
            toItemFriendShipEntity.setToId(fromId);
            toItemFriendShipEntity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            toItemFriendShipEntity.setCreateTime(System.currentTimeMillis());

            int insert = imFriendShipMapper.insert(toItemFriendShipEntity);
        } else {
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != toItemFriendShipEntity.getStatus()) {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                imFriendShipMapper.update(update, toQuery);
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO addBlack(AddFriendShipBlackReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        LambdaQueryWrapper<ImFriendShipEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ImFriendShipEntity>()
                .eq(ImFriendShipEntity::getAppId, req.getAppId())
                .eq(ImFriendShipEntity::getFromId, req.getFromId())
                .eq(ImFriendShipEntity::getToId, req.getToId());

        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(lambdaQueryWrapper);

        if (fromItem == null) {
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(req.getFromId());
            fromItem.setToId(req.getToId());
            fromItem.setAppId(req.getAppId());
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());

            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 如果存在则判断状态 如果是拉黑 则提示已拉黑
            if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
            } else {
                ImFriendShipEntity updateImFriendShip = new ImFriendShipEntity();
                updateImFriendShip.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = imFriendShipMapper.update(updateImFriendShip, lambdaQueryWrapper);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteBlack(DeleteBlackReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ImFriendShipEntity>()
                .eq(ImFriendShipEntity::getFromId, req.getFormId())
                .eq(ImFriendShipEntity::getAppId, req.getAppId())
                .eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(lambdaQueryWrapper);
        if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

        ImFriendShipEntity updateImFriendShip = new ImFriendShipEntity();
        updateImFriendShip.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int updateImFriendShipResult = imFriendShipMapper.update(updateImFriendShip, lambdaQueryWrapper);
        if (updateImFriendShipResult == 1) {
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse();
    }

    @Override
    public ResponseVO checkBlack(CheckFriendShipReq req) {
        return null;
    }
}
