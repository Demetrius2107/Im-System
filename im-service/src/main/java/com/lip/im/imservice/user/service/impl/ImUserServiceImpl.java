package com.lip.im.imservice.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lip.im.imservice.user.model.req.*;
import com.lip.im.model.ResponseVO;
import com.lip.im.model.enums.DelFlagEnum;
import com.lip.im.model.enums.UserErrorCode;
import com.lip.im.imservice.user.dao.mapper.ImUserDataMapper;
import com.lip.im.imservice.user.dao.ImUserDataEntity;
import com.lip.im.imservice.user.model.resp.GetUserInfoResp;
import com.lip.im.imservice.user.model.resp.ImportUserResp;
import com.lip.im.imservice.user.service.ImUserService;
import com.lip.im.model.exception.ApplicationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ShuKun.Li
 * @date 2024/12/18
 */
@Service
public class ImUserServiceImpl implements ImUserService {

    @Autowired
    ImUserDataMapper imUserDataMapper;

    /**
     * 导入用户资料
     *
     * @param req 导入用户请求体
     * @return ResponseVO
     */
    @Override
    public ResponseVO<ImportUserResp> importUser(ImportUserReq req) {
        // 导入用户数量大小判断
        if (req.getUserData().size() > 100) {
            return ResponseVO.errorResponse(UserErrorCode.IMPORT_SIZE_BEYOND);
        }
        ImportUserResp resp = new ImportUserResp();
        //导入成功id
        List<String> successId = new ArrayList<>();
        //导入失败id
        List<String> errorId = new ArrayList<>();

        for (ImUserDataEntity data : req.getUserData()) {
            try {
                data.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(data);
                if (insert == 1) {
                    successId.add(data.getUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(data.getUserId());
            }
        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取用户信息
     *
     * @param req 获取用户信息请求体
     * @return ResponseVO
     */
    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .in("user_id", req.getUserIds())
                .eq("del_flag", DelFlagEnum.NORMAL.getCode());

        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        HashMap<String, ImUserDataEntity> map = new HashMap<>();

        for (ImUserDataEntity data : userDataEntities) {
            map.put(data.getUserId(), data);
        }

        List<String> failUser = new ArrayList<>();
        for (String uid : req.getUserIds()) {
            if (!map.containsKey(uid)) {
                failUser.add(uid);
            }
        }

        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return ResponseVO.successResponse(resp);
    }


    /**
     * 获取单个用户信息
     *
     * @param userId 用户id
     * @param appId appId
     * @return ResponseVO
     */
    @Override
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId) {
        LambdaQueryWrapper<ImUserDataEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ImUserDataEntity>()
                .eq(ImUserDataEntity::getAppId,appId)
                .eq(ImUserDataEntity::getUserId,userId)
                .eq(ImUserDataEntity::getDelFlag,DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity imUserDataEntity = imUserDataMapper.selectOne(lambdaQueryWrapper);
        if (imUserDataEntity == null) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imUserDataEntity);
    }

    @Override
    public ResponseVO<ImportUserResp> deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (String userId : req.getUserId()) {
            QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id", req.getAppId());
            wrapper.eq("user_id", userId);
            wrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            int update;

            try {
                update = imUserDataMapper.update(entity, wrapper);
                if (update > 0) {
                    successId.add(userId);
                } else {
                    errorId.add(userId);
                }
            } catch (Exception e) {
                errorId.add(userId);
            }
        }


        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.eq("user_id", req.getUserId());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = imUserDataMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req, update);

        update.setAppId(null);
        update.setUserId(null);
        int updateRes = imUserDataMapper.update(update, queryWrapper);

        if (updateRes == 1) {
            return ResponseVO.successResponse();
        }
        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
    }

    @Override
    public ResponseVO login(LoginReq req) {
        return null;
    }

    @Override
    public ResponseVO getUserSequence(GetUserSequenceReq req) {
        return null;
    }
}
