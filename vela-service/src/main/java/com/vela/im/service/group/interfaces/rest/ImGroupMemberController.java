package com.vela.im.service.group.interfaces.rest;


import com.vela.im.service.group.application.dto.req.*;
import com.vela.im.service.group.domain.service.ImGroupMemberService;
import com.vela.im.shared.base.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: ImGroupMemberController</p>
 * <p>Description: 群组成员管理 REST 接口，处理群成员的导入、添加、移除、更新等。</p>
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
@RequestMapping("v1/group/member")
public class ImGroupMemberController {

    private final ImGroupMemberService groupMemberService;

    public ImGroupMemberController(ImGroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @RequestMapping("/importGroupMember")
    public Result importGroupMember(@RequestBody @Validated ImportGroupMemberRequest importGroupMemberRequest, Integer appId, String identifier)  {
        importGroupMemberRequest.setAppId(appId);
        importGroupMemberRequest.setOperater(identifier);
        return groupMemberService.importGroupMember(importGroupMemberRequest);
    }

    @RequestMapping("/add")
    public Result addMember(@RequestBody @Validated AddGroupMemberRequest addGroupMemberRequest, Integer appId, String identifier)  {
        addGroupMemberRequest.setAppId(appId);
        addGroupMemberRequest.setOperater(identifier);
        return groupMemberService.addMember(addGroupMemberRequest);
    }

    @RequestMapping("/remove")
    public Result removeMember(@RequestBody @Validated RemoveGroupMemberRequest removeGroupMemberRequest, Integer appId, String identifier)  {
        removeGroupMemberRequest.setAppId(appId);
        removeGroupMemberRequest.setOperater(identifier);
        return groupMemberService.removeMember(removeGroupMemberRequest);
    }

    @RequestMapping("/update")
    public Result updateGroupMember(@RequestBody @Validated UpdateGroupMemberRequest updateGroupMemberRequest, Integer appId, String identifier)  {
        updateGroupMemberRequest.setAppId(appId);
        updateGroupMemberRequest.setOperater(identifier);
        return groupMemberService.updateGroupMember(updateGroupMemberRequest);
    }

    @RequestMapping("/speak")
    public Result speak(@RequestBody @Validated SpeakMemberRequest speakMemberRequest, Integer appId, String identifier)  {
        speakMemberRequest.setAppId(appId);
        speakMemberRequest.setOperater(identifier);
        return groupMemberService.speak(speakMemberRequest);
    }

}
