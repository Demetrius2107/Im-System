package com.vela.im.service.friendship.domain.service;


import com.vela.im.service.friendship.application.dto.resp.ImportFriendShipResp;
import com.vela.im.service.friendship.application.dto.req.*;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.types.RequestBase;
import com.vela.im.shared.types.SyncReq;

import java.util.List;

/**
 * @author wanqiu
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
