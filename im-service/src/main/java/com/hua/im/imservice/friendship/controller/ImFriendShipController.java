package com.hua.im.imservice.friendship.controller;

import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imservice.friendship.model.req.DeleteFriendReq;
import com.hua.im.imservice.friendship.model.req.UpdateFriendReq;
import com.hua.im.imservice.friendship.service.ImFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shukun.Li
 */
@RestController
@RequestMapping("/v1/friendShip")
public class ImFriendShipController {

    @Autowired
    private ImFriendService imFriendShipService;


    @RequestMapping("/updateFriend")
    public ResponseVO updateFriend(@RequestBody @Validated UpdateFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.updateFriend(req);
    }

    @RequestMapping("/deleteFriend")
    public ResponseVO deleteFriend(@RequestBody @Validated DeleteFriendReq req){
        return imFriendShipService.deleteFriend(req);
    }

}
