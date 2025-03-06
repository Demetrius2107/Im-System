package com.lip.im.imservice.friendship.service;


import com.lip.im.imservice.friendship.model.resp.ImportFriendShipResp;
import com.lip.im.imservice.friendship.model.req.*;
import com.lip.im.model.ResponseVO;
import com.lip.im.model.model.RequestBase;
import com.lip.im.model.model.SyncReq;

import java.util.List;

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

    public ResponseVO doAddFriend(RequestBase requestBase,String fromId, FriendDto dto, Integer appId);

    public ResponseVO checkFriendship(CheckFriendShipReq req);

    public ResponseVO addBlack(AddFriendShipBlackReq req);

    public ResponseVO deleteBlack(DeleteBlackReq req);

    public ResponseVO checkBlck(CheckFriendShipReq req);

    public ResponseVO syncFriendshipList(SyncReq req);

    public List<String> getAllFriendId(String userId, Integer appId);


}
