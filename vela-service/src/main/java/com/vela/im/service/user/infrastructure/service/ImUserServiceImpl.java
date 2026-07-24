package com.vela.im.service.user.infrastructure.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.vela.im.service.group.domain.service.ImGroupService;
import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.service.user.infrastructure.persistence.mapper.ImUserDataMapper;
import com.vela.im.service.user.application.dto.req.*;
import com.vela.im.service.user.application.dto.resp.GetUserInfoResp;
import com.vela.im.service.user.application.dto.resp.ImportUserResp;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.service.application.utils.CallbackService;
import com.vela.im.service.application.utils.MessageProducer;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.DelFlagEnum;
import com.vela.im.shared.types.enums.UserErrorCode;
import com.vela.im.shared.types.enums.command.UserEventCommand;
import com.vela.im.shared.exception.ApplicationException;
import com.vela.im.codec.pack.user.UserModifyPack;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: ImUserServiceImpl</p>
 * <p>Description: 用户管理实现，处理用户的导入、删除、查询、信息修改、登录等。</p>
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
public class ImUserServiceImpl implements ImUserService {

    private final ImUserDataMapper imUserDataMapper;
    private final AppConfig appConfig;
    private final CallbackService callbackService;
    private final MessageProducer messageProducer;
    private final StringRedisTemplate stringRedisTemplate;
    private final ImGroupService imGroupService;

    public ImUserServiceImpl(ImUserDataMapper imUserDataMapper,
                             AppConfig appConfig,
                             CallbackService callbackService,
                             MessageProducer messageProducer,
                             StringRedisTemplate stringRedisTemplate,
                             ImGroupService imGroupService) {
        this.imUserDataMapper = imUserDataMapper;
        this.appConfig = appConfig;
        this.callbackService = callbackService;
        this.messageProducer = messageProducer;
        this.stringRedisTemplate = stringRedisTemplate;
        this.imGroupService = imGroupService;
    }

    @Override
    public Result importUser(ImportUserReq req) {

        if(req.getUserData().size() > 100){
            return Result.fail(UserErrorCode.IMPORT_SIZE_BEYOND);
        }

        ImportUserResp resp = new ImportUserResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImUserDataEntity data:
                req.getUserData()) {
            try {
                data.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(data);
                if(insert == 1){
                    successId.add(data.getUserId());
                }
            }catch (Exception e){
                e.printStackTrace();
                errorId.add(data.getUserId());
            }
        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);
        return Result.ok(resp);
    }

    @Override
    public Result<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",req.getAppId());
        queryWrapper.in("user_id",req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        HashMap<String, ImUserDataEntity> map = new HashMap<>();

        for (ImUserDataEntity data:
                userDataEntities) {
            map.put(data.getUserId(),data);
        }

        List<String> failUser = new ArrayList<>();
        for (String uid:
                req.getUserIds()) {
            if(!map.containsKey(uid)){
                failUser.add(uid);
            }
        }

        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return Result.ok(resp);
    }

    @Override
    public Result<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("app_id",appId);
        objectQueryWrapper.eq("user_id",userId);
        objectQueryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity ImUserDataEntity = imUserDataMapper.selectOne(objectQueryWrapper);
        if(ImUserDataEntity == null){
            return Result.fail(UserErrorCode.USER_IS_NOT_EXIST);
        }

        return Result.ok(ImUserDataEntity);
    }

    @Override
    public Result deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList();
        List<String> successId = new ArrayList();

        for (String userId:
                req.getUserId()) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("app_id",req.getAppId());
            wrapper.eq("user_id",userId);
            wrapper.eq("del_flag",DelFlagEnum.NORMAL.getCode());
            int update = 0;

            try {
                update =  imUserDataMapper.update(entity, wrapper);
                if(update > 0){
                    successId.add(userId);
                }else{
                    errorId.add(userId);
                }
            }catch (Exception e){
                errorId.add(userId);
            }
        }

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return Result.ok(resp);
    }

    @Override
    @Transactional
    public Result modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper query = new QueryWrapper<>();
        query.eq("app_id",req.getAppId());
        query.eq("user_id",req.getUserId());
        query.eq("del_flag",DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = imUserDataMapper.selectOne(query);
        if(user == null){
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req,update);

        update.setAppId(null);
        update.setUserId(null);
        int update1 = imUserDataMapper.update(update, query);
        if(update1 == 1){
            // 用户数据发生变更后 将数据从IM发送要其他端
            UserModifyPack pack = new UserModifyPack();
            BeanUtils.copyProperties(req,pack);
            messageProducer.sendToUser(req.getUserId(),req.getClientType(),req.getImei(),
                    UserEventCommand.USER_MODIFY,pack,req.getAppId());

            // 回调
            if(appConfig.isModifyUserAfterCallback()){
                callbackService.callback(req.getAppId(),
                        Constants.CallbackCommand.ModifyUserAfter,
                        JSONObject.toJSONString(req));
            }
            return Result.ok();
        }
        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
    }

    @Override
    public Result login(LoginReq req) {
        return Result.ok();
    }

    @Override
    public Result getUserSequence(GetUserSequenceReq req) {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(req.getAppId() + ":" + Constants.RedisConstants.SeqPrefix + ":" + req.getUserId());
        Long groupSeq = imGroupService.getUserGroupMaxSeq(req.getUserId(),req.getAppId());
        map.put(Constants.SeqConstants.Group,groupSeq);
        return Result.ok(map);
    }
}
