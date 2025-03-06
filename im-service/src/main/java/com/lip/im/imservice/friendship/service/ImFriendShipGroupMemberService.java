package com.lip.im.imservice.friendship.service;


import com.lip.im.imservice.friendship.model.req.AddFriendShipGroupMemberReq;
import com.lip.im.imservice.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.lip.im.model.ResponseVO;

/**
 * @author Shukun.Li
 */
public interface ImFriendShipGroupMemberService {

    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req);

    public ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req);

    public int doAddGroupMember(Long groupId, String toId);

    public int clearGroupMember(Long groupId);

}
