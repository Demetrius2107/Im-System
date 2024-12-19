package com.hua.im.imservice.friendship.service;

import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imcommon.model.RequestBase;
import com.hua.im.imservice.friendship.model.req.*;

/**
 * @author Shukun.Li
 */
public interface ImFriendService {

    public ResponseVO importFriendShip(ImportFriendShipReq req);

    public ResponseVO addFriend(AddFriendReq req);

    public ResponseVO updateFriend(UpdateFriendReq req);

    public ResponseVO deleteFriend(DeleteFriendReq req);

    public ResponseVO deleteAllFriend(DeleteFriendReq req);

    public ResponseVO getAllFriendShip(GetAllFriendShipReq req);

    public ResponseVO getRelation(GetRelationReq req);

    public ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);

    public ResponseVO addBlack(AddFriendShipBlackReq req);

    public ResponseVO deleteBlack(DeleteBlackReq req);

    public ResponseVO checkBlack(CheckFriendShipReq req);

}
