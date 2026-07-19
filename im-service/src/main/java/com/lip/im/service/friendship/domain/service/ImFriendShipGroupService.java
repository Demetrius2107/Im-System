package com.lip.im.service.friendship.domain.service;


import com.lip.im.service.friendship.domain.entity.ImFriendShipGroupEntity;
import com.lip.im.service.friendship.application.dto.req.AddFriendShipGroupReq;
import com.lip.im.service.friendship.application.dto.req.DeleteFriendShipGroupReq;
import com.lip.im.shared.base.ResponseVO;

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
    public ResponseVO addGroup(AddFriendShipGroupReq req);

    /**
     * 删除好友组
     *
     * @param req request
     * @return response
     */
    public ResponseVO deleteGroup(DeleteFriendShipGroupReq req);

    /**
     * 获取好友组
     *
     * @param fromId
     * @param groupName
     * @param appId
     * @return
     */
    public ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);

    public Long updateSeq(String fromId, String groupName, Integer appId);

}
