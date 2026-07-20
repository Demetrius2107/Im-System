package com.vela.im.service.friendship.interfaces.rest;


import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupMemberReq;
import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupReq;
import com.vela.im.service.friendship.application.dto.req.DeleteFriendShipGroupMemberReq;
import com.vela.im.service.friendship.application.dto.req.DeleteFriendShipGroupReq;
import com.vela.im.service.friendship.domain.service.ImFriendShipGroupMemberService;
import com.vela.im.service.friendship.domain.service.ImFriendShipGroupService;
import com.vela.im.shared.base.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: ImFriendShipGroupController</p>
 * <p>Description: 好友分组 REST 接口，处理好友分组的创建、删除、成员管理等。</p>
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
@RequestMapping("v1/friendship/group")
public class ImFriendShipGroupController {

    private final ImFriendShipGroupService imFriendShipGroupService;
    private final ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    public ImFriendShipGroupController(ImFriendShipGroupService imFriendShipGroupService,
                                       ImFriendShipGroupMemberService imFriendShipGroupMemberService) {
        this.imFriendShipGroupService = imFriendShipGroupService;
        this.imFriendShipGroupMemberService = imFriendShipGroupMemberService;
    }


    @RequestMapping("/add")
    public Result add(@RequestBody @Validated AddFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupService.addGroup(req);
    }

    @RequestMapping("/del")
    public Result del(@RequestBody @Validated DeleteFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupService.deleteGroup(req);
    }

    @RequestMapping("/member/add")
    public Result memberAdd(@RequestBody @Validated AddFriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.addGroupMember(req);
    }

    @RequestMapping("/member/del")
    public Result memberdel(@RequestBody @Validated DeleteFriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.delGroupMember(req);
    }


}
