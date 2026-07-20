package com.vela.im.service.user.interfaces.rest;

import com.vela.im.service.user.application.dto.req.*;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.service.user.domain.service.ImUserStatusService;
import com.vela.im.service.application.utils.ZKit;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.route.RouteHandle;
import com.vela.im.shared.route.RouteInfo;
import com.vela.im.shared.utils.RouteInfoParseUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: ImUserController</p>
 * <p>Description: 用户管理 REST 接口，处理用户导入、删除、登录、在线状态查询等。</p>
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
@RequestMapping("v1/user")
public class ImUserController {

    private final ImUserService imUserService;
    private final RouteHandle routeHandle;
    private final ImUserStatusService imUserStatusService;
    private final ZKit zKit;

    public ImUserController(ImUserService imUserService,
                            RouteHandle routeHandle,
                            ImUserStatusService imUserStatusService,
                            ZKit zKit) {
        this.imUserService = imUserService;
        this.routeHandle = routeHandle;
        this.imUserStatusService = imUserStatusService;
        this.zKit = zKit;
    }

    @RequestMapping("importUser")
    public Result importUser(@RequestBody ImportUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.importUser(req);
    }


    @RequestMapping("/deleteUser")
    public Result deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }

    /**
     * @param req
     * @return com.lld.im.common.Result
     * @description im的登录接口，返回im地址
     * @author wanqiu
     */
    @RequestMapping("/login")
    public Result login(@RequestBody @Validated LoginReq req, Integer appId) {
        req.setAppId(appId);

        Result login = imUserService.login(req);
        if (login.isOk()) {
            List<String> allNode = new ArrayList<>();
            if (req.getClientType() == com.lld.im.common.ClientType.WEB.getCode()) {
                allNode = zKit.getAllWebNode();
            } else {
                allNode = zKit.getAllTcpNode();
            }
            String s = routeHandle.routeServer(allNode, req
                    .getUserId());
            RouteInfo parse = RouteInfoParseUtil.parse(s);
            return Result.ok(parse);
        }

        return Result.fail();
    }

    @RequestMapping("/getUserSequence")
    public Result getUserSequence(@RequestBody @Validated
                                      GetUserSequenceReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getUserSequence(req);
    }

    @RequestMapping("/subscribeUserOnlineStatus")
    public Result subscribeUserOnlineStatus(@RequestBody @Validated
                                                SubscribeUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperater(identifier);
        imUserStatusService.subscribeUserOnlineStatus(req);
        return Result.ok();
    }

    @RequestMapping("/setUserCustomerStatus")
    public Result setUserCustomerStatus(@RequestBody @Validated
                                            SetUserCustomerStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperater(identifier);
        imUserStatusService.setUserCustomerStatus(req);
        return Result.ok();
    }

    @RequestMapping("/queryFriendOnlineStatus")
    public Result queryFriendOnlineStatus(@RequestBody @Validated
                                              PullFriendOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperater(identifier);
        return Result.ok(imUserStatusService.queryFriendOnlineStatus(req));
    }

    @RequestMapping("/queryUserOnlineStatus")
    public Result queryUserOnlineStatus(@RequestBody @Validated
                                            PullUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperater(identifier);
        return Result.ok(imUserStatusService.queryUserOnlineStatus(req));
    }


}
