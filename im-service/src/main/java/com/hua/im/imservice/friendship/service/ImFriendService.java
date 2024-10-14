package com.hua.im.imservice.friendship.service;

import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imservice.friendship.model.req.AddFriendReq;
import com.hua.im.imservice.friendship.model.req.ImportFriendShipReq;

public interface ImFriendService {

    public ResponseVO importFriendShip(ImportFriendShipReq req);

    public ResponseVO addFriend(AddFriendReq req);

    public ResponseVO updateFriend();

}
