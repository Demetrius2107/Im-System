package com.vela.im.service.group.interfaces.rest;


import com.vela.im.service.group.application.dto.req.*;
import com.vela.im.service.group.domain.service.GroupMessageService;
import com.vela.im.service.group.domain.service.ImGroupService;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.types.SyncReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: ImGroupController</p>
 * <p>Description: 群组管理 REST 接口，处理群组的创建、导入、更新、解散、禁言等。</p>
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
@RequestMapping("v1/group")
public class ImGroupController {

    private final ImGroupService groupService;
    private final GroupMessageService groupMessageService;

    public ImGroupController(ImGroupService groupService,
                             GroupMessageService groupMessageService) {
        this.groupService = groupService;
        this.groupMessageService = groupMessageService;
    }

    @RequestMapping("/importGroup")
    public Result importGroup(@RequestBody @Validated ImportGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupService.importGroup(req);
    }

    @RequestMapping("/createGroup")
    public Result createGroup(@RequestBody @Validated CreateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupService.createGroup(req);
    }

    @RequestMapping("/getGroupInfo")
    public Result getGroupInfo(@RequestBody @Validated GetGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return groupService.getGroup(req);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody @Validated UpdateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupService.updateBaseGroupInfo(req);
    }

    @RequestMapping("/getJoinedGroup")
    public Result getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupService.getJoinedGroup(req);
    }


    @RequestMapping("/destroyGroup")
    public Result destroyGroup(@RequestBody @Validated DestroyGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupService.destroyGroup(req);
    }

    @RequestMapping("/transferGroup")
    public Result transferGroup(@RequestBody @Validated TransferGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupService.transferGroup(req);
    }

    @RequestMapping("/forbidSendMessage")
    public Result forbidSendMessage(@RequestBody @Validated MuteGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupService.muteGroup(req);
    }

    @RequestMapping("/sendMessage")
    public Result sendMessage(@RequestBody @Validated SendGroupMessageReq
                                              req, Integer appId,
                                  String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return Result.ok(groupMessageService.send(req));
    }

    @RequestMapping("/syncJoinedGroup")
    public Result syncJoinedGroup(@RequestBody @Validated SyncReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        return groupService.syncJoinedGroupList(req);
    }

}
