package com.vela.im.service.friendship.domain.service;


import com.vela.im.service.friendship.application.dto.req.ApproveFriendRequestReq;
import com.vela.im.service.friendship.application.dto.req.FriendDto;
import com.vela.im.service.friendship.application.dto.req.ReadFriendShipRequestReq;
import com.vela.im.shared.base.ResponseVO;

/**
 * @author wanqiu
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
