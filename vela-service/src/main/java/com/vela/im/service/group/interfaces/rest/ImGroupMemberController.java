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
    public Result importGroupMember(@RequestBody @Validated ImportGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupMemberService.importGroupMember(req);
    }

    @RequestMapping("/add")
    public Result addMember(@RequestBody @Validated AddGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupMemberService.addMember(req);
    }

    @RequestMapping("/remove")
    public Result removeMember(@RequestBody @Validated RemoveGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupMemberService.removeMember(req);
    }

    @RequestMapping("/update")
    public Result updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupMemberService.updateGroupMember(req);
    }

    @RequestMapping("/speak")
    public Result speak(@RequestBody @Validated SpeaMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperater(identifier);
        return groupMemberService.speak(req);
    }

}
