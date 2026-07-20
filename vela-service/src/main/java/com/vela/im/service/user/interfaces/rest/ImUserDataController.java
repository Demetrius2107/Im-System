package com.vela.im.service.user.interfaces.rest;


import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.service.user.application.dto.req.GetUserInfoReq;
import com.vela.im.service.user.application.dto.req.ModifyUserInfoReq;
import com.vela.im.service.user.application.dto.req.UserId;
import com.vela.im.service.user.application.dto.resp.GetUserInfoResp;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.shared.base.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: ImUserDataController</p>
 * <p>Description: 用户数据 REST 接口，处理用户信息查询、修改等请求。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-06
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {

    private static final Logger logger = LoggerFactory.getLogger(ImUserDataController.class);

    private final ImUserService imUserService;

    public ImUserDataController(ImUserService imUserService) {
        this.imUserService = imUserService;
    }

    @RequestMapping("/getUserInfo")
    public ResponseVO<GetUserInfoResp> getUserInfo(@RequestBody GetUserInfoReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getUserInfo(req);
    }

    @RequestMapping("/getSingleUserInfo")
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(@RequestBody @Validated UserId req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
    }

    @RequestMapping("/modifyUserInfo")
    public ResponseVO modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId){
        req.setAppId(appId);
        return imUserService.modifyUserInfo(req);
    }

}
