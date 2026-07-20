package com.vela.im.service.friendship.interfaces.rest;

import com.vela.im.service.friendship.application.dto.req.ApproveFriendRequestReq;
import com.vela.im.service.friendship.application.dto.req.GetFriendShipRequestReq;
import com.vela.im.service.friendship.application.dto.req.ReadFriendShipRequestReq;
import com.vela.im.service.friendship.domain.service.ImFriendShipRequestService;
import com.vela.im.shared.base.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>Title: ImFriendShipRequestController</p>
 * <p>Description: 好友请求 REST 接口，处理好友申请的审批、查询、已读等。</p>
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
@RequestMapping("v1/friendshipRequest")
public class ImFriendShipRequestController {

    private final ImFriendShipRequestService imFriendShipRequestService;

    public ImFriendShipRequestController(ImFriendShipRequestService imFriendShipRequestService) {
        this.imFriendShipRequestService = imFriendShipRequestService;
    }

    @RequestMapping("/approveFriendRequest")
    public ResponseVO approveFriendRequest(@RequestBody @Validated
                                           ApproveFriendRequestReq req, Integer appId, String identifier){
        req.setAppId(appId);
        req.setOperater(identifier);
        return imFriendShipRequestService.approveFriendRequest(req);
    }
    @RequestMapping("/getFriendRequest")
    public ResponseVO getFriendRequest(@RequestBody @Validated GetFriendShipRequestReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipRequestService.getFriendRequest(req.getFormId(),req.getAppId());
    }

    @RequestMapping("/readFriendShipRequestReq")
    public ResponseVO readFriendShipRequestReq(@RequestBody @Validated ReadFriendShipRequestReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipRequestService.readFriendShipRequestReq(req);
    }


}
