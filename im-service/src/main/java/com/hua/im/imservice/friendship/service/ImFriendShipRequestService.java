package com.hua.im.imservice.friendship.service;

import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imservice.friendship.model.req.ApproveFriendRequestReq;
import com.hua.im.imservice.friendship.model.req.FriendDto;
import com.hua.im.imservice.friendship.model.req.ReadFriendShipRequestReq;

/**
 * @author Shukun.Li
 */
public interface ImFriendShipRequestService {

    public ResponseVO addFriendShipRequest(String fromId, FriendDto dto,Integer appId);

    public ResponseVO approveFriendRequest(ApproveFriendRequestReq req);

    public ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req);

    public ResponseVO getFriendRequest(String fromId,Integer appId);

}
