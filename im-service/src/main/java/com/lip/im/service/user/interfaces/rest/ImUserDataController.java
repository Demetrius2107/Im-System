package com.lip.im.service.user.interfaces.rest;


import com.lip.im.service.user.domain.entity.ImUserDataEntity;
import com.lip.im.service.user.application.dto.req.GetUserInfoReq;
import com.lip.im.service.user.application.dto.req.ModifyUserInfoReq;
import com.lip.im.service.user.application.dto.req.UserId;
import com.lip.im.service.user.application.dto.resp.GetUserInfoResp;
import com.lip.im.service.user.domain.service.ImUserService;
import com.lip.im.shared.base.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shukun.Li
 */
@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {

    private static final Logger logger = LoggerFactory.getLogger(ImUserDataController.class);

    @Autowired
    ImUserService imUserService;

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
