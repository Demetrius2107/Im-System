package com.hua.im.imservice.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imcommon.enums.DelFlagEnum;
import com.hua.im.imcommon.enums.UserErrorCode;
import com.hua.im.imcommon.exception.ApplicationException;
import com.hua.im.imservice.user.dao.ImUserDataEntity;
import com.hua.im.imservice.user.dao.mapper.ImUserDataMapper;
import com.hua.im.imservice.user.model.req.DeleteUserReq;
import com.hua.im.imservice.user.model.req.GetUserInfoReq;
import com.hua.im.imservice.user.model.req.ImportUserReq;
import com.hua.im.imservice.user.model.req.ModifyUserInfoReq;
import com.hua.im.imservice.user.model.resp.GetUserInfoResp;
import com.hua.im.imservice.user.model.resp.ImportUserResp;
import com.hua.im.imservice.user.service.ImUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ImUserServiceImpl implements ImUserService {

    @Autowired
    ImUserDataMapper imUserDataMapper;

    /**
     * 导入用户资料
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO importUser(ImportUserReq req) {

        if (req.getUserData().size() > 100) {
            return ResponseVO.errorResponse(UserErrorCode.IMPORT_SIZE_BEYOND);
        }

        ImportUserResp resp = new ImportUserResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImUserDataEntity data : req.getUserData()) {
            req.getUserData();
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
     * @param req
     * @return
     */
    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.in("user_id", req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

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
     * @param userId
     * @param appId
     * @return
     */
    @Override
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("app_id", appId);
        objectQueryWrapper.eq("user_id", userId);
        objectQueryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity imUserDataEntity = imUserDataMapper.selectOne(objectQueryWrapper);
        if (imUserDataEntity == null) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imUserDataEntity);
    }

    @Override
    public ResponseVO deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (String userId : req.getUserId()) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("app_id", req.getAppId());
            wrapper.eq("user_id", userId);
            wrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            int update = 0;

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
        return null;
    }

    @Override
    @Transactional
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",req.getAppId());
        queryWrapper.eq("user_id",req.getUserId());
        queryWrapper.eq("del_flag",DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = imUserDataMapper.selectOne(queryWrapper);
        if(user == null)
        {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req,update);

        update.setAppId(null);
        update.setUserId(null);
        int updateRes = imUserDataMapper.update(update,queryWrapper);

        if(updateRes == 1){
            return ResponseVO.successResponse();
        }
        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
    }
}
