package com.lip.im.imservice.friendship.service;


import com.lip.im.imservice.friendship.model.resp.ImportFriendShipResp;
import com.lip.im.imservice.friendship.model.req.*;
import com.lip.im.model.ResponseVO;
import com.lip.im.model.model.RequestBase;

/**
 * @author Shukun.Li
 */
public interface ImFriendService {

    /**
     * 导入好友关系链
     *
     * @param req request
     * @return response
     */
    public ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req);

    /**
     * 新增好友
     *
     * @param req request
     * @return response
     */
    public ResponseVO addFriend(AddFriendReq req);

    /**
     * 更新好友
     *
     * @param req request
     * @return response
     */
    public ResponseVO updateFriend(UpdateFriendReq req);

    /**
     * 删除好友
     *
     * @param req request
     * @return response
     */
    public ResponseVO deleteFriend(DeleteFriendReq req);

    /**
     * 删除所有好友
     *
     * @param req
     * @return
     */
    public ResponseVO deleteAllFriend(DeleteFriendReq req);


    public ResponseVO getAllFriendShip(GetAllFriendShipReq req);

    public ResponseVO getRelation(GetRelationReq req);

    public ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);

    public ResponseVO addBlack(AddFriendShipBlackReq req);

    public ResponseVO deleteBlack(DeleteBlackReq req);

    public ResponseVO checkBlack(CheckFriendShipReq req);

}
