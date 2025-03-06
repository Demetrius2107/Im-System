package com.lip.im.imservice.friendship.controller;

import com.hua.im.imcommon.ResponseVO;
import com.lip.im.imservice.friendship.model.req.AddFriendShipGroupMemberReq;
import com.lip.im.imservice.friendship.model.req.AddFriendShipGroupReq;
import com.lip.im.imservice.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.lip.im.imservice.friendship.model.req.DeleteFriendShipGroupReq;
import com.lip.im.imservice.friendship.service.ImFriendShipGroupMemberService;
import com.lip.im.imservice.friendship.service.ImFriendShipGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shukun.Li
 */
@RestController
@RequestMapping("/v1/friendship/group")
public class ImFriendShipGroupController {

    @Autowired
    ImFriendShipGroupService imFriendShipGroupService;

    @Autowired
    ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    @RequestMapping("/add")
    public ResponseVO add(@RequestBody @Validated AddFriendShipGroupReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupService.addGroup(req);
    }

    @RequestMapping("/del")
    public ResponseVO del(@RequestBody @Validated DeleteFriendShipGroupReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupService.deleteGroup(req);
    }

    @RequestMapping("/member/add")
    public ResponseVO memberAdd(@RequestBody @Validated AddFriendShipGroupMemberReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.addGroupMember(req);
    }

    @RequestMapping("/member/del")
    public ResponseVO memberDel(@RequestBody @Validated DeleteFriendShipGroupMemberReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.delGroupMember(req);
    }


}
