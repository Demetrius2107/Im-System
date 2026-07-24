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
    public Result importGroup(@RequestBody @Validated ImportGroupRequest importGroupRequest, Integer appId, String identifier)  {
        importGroupRequest.setAppId(appId);
        importGroupRequest.setOperater(identifier);
        return groupService.importGroup(importGroupRequest);
    }

    @RequestMapping("/createGroup")
    public Result createGroup(@RequestBody @Validated CreateGroupRequest createGroupRequest, Integer appId, String identifier)  {
        createGroupRequest.setAppId(appId);
        createGroupRequest.setOperater(identifier);
        return groupService.createGroup(createGroupRequest);
    }

    @RequestMapping("/getGroupInfo")
    public Result getGroupInfo(@RequestBody @Validated GetGroupRequest getGroupRequest, Integer appId)  {
        getGroupRequest.setAppId(appId);
        return groupService.getGroup(getGroupRequest);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody @Validated UpdateGroupRequest updateGroupRequest, Integer appId, String identifier)  {
        updateGroupRequest.setAppId(appId);
        updateGroupRequest.setOperater(identifier);
        return groupService.updateBaseGroupInfo(updateGroupRequest);
    }

    @RequestMapping("/getJoinedGroup")
    public Result getJoinedGroup(@RequestBody @Validated GetJoinedGroupRequest getJoinedGroupRequest, Integer appId, String identifier)  {
        getJoinedGroupRequest.setAppId(appId);
        getJoinedGroupRequest.setOperater(identifier);
        return groupService.getJoinedGroup(getJoinedGroupRequest);
    }


    @RequestMapping("/destroyGroup")
    public Result destroyGroup(@RequestBody @Validated DestroyGroupRequest destroyGroupRequest, Integer appId, String identifier)  {
        destroyGroupRequest.setAppId(appId);
        destroyGroupRequest.setOperater(identifier);
        return groupService.destroyGroup(destroyGroupRequest);
    }

    @RequestMapping("/transferGroup")
    public Result transferGroup(@RequestBody @Validated TransferGroupRequest transferGroupRequest, Integer appId, String identifier)  {
        transferGroupRequest.setAppId(appId);
        transferGroupRequest.setOperater(identifier);
        return groupService.transferGroup(transferGroupRequest);
    }

    @RequestMapping("/forbidSendMessage")
    public Result forbidSendMessage(@RequestBody @Validated MuteGroupRequest muteGroupRequest, Integer appId, String identifier)  {
        muteGroupRequest.setAppId(appId);
        muteGroupRequest.setOperater(identifier);
        return groupService.muteGroup(muteGroupRequest);
    }

    @RequestMapping("/sendMessage")
    public Result sendMessage(@RequestBody @Validated
                                              SendGroupMessageRequest req, Integer appId,
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
