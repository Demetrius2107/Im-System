package com.vela.im.service.friendship.domain.service;


import com.vela.im.service.friendship.application.dto.resp.ImportFriendShipResp;
import com.vela.im.service.friendship.application.dto.req.*;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.types.RequestBase;
import com.vela.im.shared.types.SyncReq;

import java.util.List;

/**
 * @author wanqiu
 */
public interface ImFriendService {

    public Result importFriendShip(ImportFriendShipReq req);

    public Result addFriend(AddFriendReq req);

    public Result updateFriend(UpdateFriendReq req);

    public Result deleteFriend(DeleteFriendReq req);

    public Result deleteAllFriend(DeleteFriendReq req);

    public Result getAllFriendShip(GetAllFriendShipReq req);

    public Result getRelation(GetRelationReq req);

    public Result doAddFriend(RequestBase requestBase,String fromId, FriendDto dto, Integer appId);

    public Result checkFriendship(CheckFriendShipReq req);

    public Result addBlack(AddFriendShipBlackReq req);

    public Result deleteBlack(DeleteBlackReq req);

    public Result checkBlck(CheckFriendShipReq req);

    public Result syncFriendshipList(SyncReq req);

    public List<String> getAllFriendId(String userId, Integer appId);


}
