package com.vela.im.service.friendship.interfaces.rest;

import com.vela.im.service.friendship.application.dto.req.*;
import com.vela.im.service.friendship.domain.service.ImFriendService;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.types.SyncReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: ImFriendShipController</p>
 * <p>Description: 好友关系 REST 接口，处理好友导入、添加、删除、更新、黑名单、关系链查询等。</p>
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
@RequestMapping("v1/friendship")
public class ImFriendShipController {

    private final ImFriendService imFriendShipService;

    public ImFriendShipController(ImFriendService imFriendShipService) {
        this.imFriendShipService = imFriendShipService;
    }

    @RequestMapping("/importFriendShip")
    public ResponseVO importFriendShip(@RequestBody @Validated ImportFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.importFriendShip(req);
    }

    @RequestMapping("/addFriend")
    public ResponseVO addFriend(@RequestBody @Validated AddFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.addFriend(req);
    }

    @RequestMapping("/updateFriend")
    public ResponseVO updateFriend(@RequestBody @Validated UpdateFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.updateFriend(req);
    }

    @RequestMapping("/deleteFriend")
    public ResponseVO deleteFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.deleteFriend(req);
    }

    @RequestMapping("/deleteAllFriend")
    public ResponseVO deleteAllFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.deleteAllFriend(req);
    }

    @RequestMapping("/getAllFriendShip")
    public ResponseVO getAllFriendShip(@RequestBody @Validated GetAllFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.getAllFriendShip(req);
    }

    @RequestMapping("/getRelation")
    public ResponseVO getRelation(@RequestBody @Validated GetRelationReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.getRelation(req);
    }

    @RequestMapping("/checkFriend")
    public ResponseVO checkFriend(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.checkFriendship(req);
    }

    @RequestMapping("/addBlack")
    public ResponseVO addBlack(@RequestBody @Validated AddFriendShipBlackReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.addBlack(req);
    }

    @RequestMapping("/deleteBlack")
    public ResponseVO deleteBlack(@RequestBody @Validated DeleteBlackReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.deleteBlack(req);
    }

    @RequestMapping("/checkBlck")
    public ResponseVO checkBlck(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.checkBlck(req);
    }


    @RequestMapping("/syncFriendshipList")
    public ResponseVO syncFriendshipList(@RequestBody @Validated
                                         SyncReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.syncFriendshipList(req);
    }
}
