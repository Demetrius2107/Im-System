package com.hua.im.imservice.friendship.service;

import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imservice.friendship.model.req.ApproveFriendRequestReq;
import com.hua.im.imservice.friendship.model.req.FriendDto;
import com.hua.im.imservice.friendship.model.req.ReadFriendShipRequestReq;

/**
 * @author Shukun.Li
 */
public interface ImFriendShipRequestService {

    /**
     * 新增好友关系请求
     *
     * @param fromId
     * @param dto
     * @param appId
     * @return responseVO
     */
    public ResponseVO addFriendShipRequest(String fromId, FriendDto dto, Integer appId);

    /**
     * 验证好友关系请求
     *
     * @param req
     * @return responseVO
     */
    public ResponseVO approveFriendRequest(ApproveFriendRequestReq req);

    /**
     * 查询/浏览好友关系请求
     *
     * @param req
     * @return responseVO
     */
    public ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req);

    /**
     * 获取好友请求
     *
     * @param fromId
     * @param appId
     * @return responseVO
     */
    public ResponseVO getFriendRequest(String fromId, Integer appId);

}
