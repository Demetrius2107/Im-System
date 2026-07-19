package com.lip.im.service.friendship.interfaces.rest;

import com.lip.im.service.friendship.application.dto.req.ApproveFriendRequestReq;
import com.lip.im.service.friendship.application.dto.req.GetFriendShipRequestReq;
import com.lip.im.service.friendship.application.dto.req.ReadFriendShipRequestReq;
import com.lip.im.service.friendship.domain.service.ImFriendShipRequestService;
import com.lip.im.shared.base.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("v1/friendshipRequest")
public class ImFriendShipRequestController {

    @Autowired
    ImFriendShipRequestService imFriendShipRequestService;

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
