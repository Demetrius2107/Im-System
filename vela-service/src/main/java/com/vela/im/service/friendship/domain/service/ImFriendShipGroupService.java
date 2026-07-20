package com.vela.im.service.friendship.domain.service;


import com.vela.im.service.friendship.domain.entity.ImFriendShipGroupEntity;
import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupReq;
import com.vela.im.service.friendship.application.dto.req.DeleteFriendShipGroupReq;
import com.vela.im.shared.base.Result;

/**
 * @author wanqiu
 * @date 2024/12/19
 */
public interface ImFriendShipGroupService {

    /**
     * 新增好友组
     *
     * @param req request
     * @return response
     */
    public Result addGroup(AddFriendShipGroupReq req);

    /**
     * 删除好友组
     *
     * @param req request
     * @return response
     */
    public Result deleteGroup(DeleteFriendShipGroupReq req);

    /**
     * 获取好友组
     *
     * @param fromId
     * @param groupName
     * @param appId
     * @return
     */
    public Result<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);

    public Long updateSeq(String fromId, String groupName, Integer appId);

}
