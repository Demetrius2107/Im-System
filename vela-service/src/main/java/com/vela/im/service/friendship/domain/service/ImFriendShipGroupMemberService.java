package com.vela.im.service.friendship.domain.service;


import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupMemberReq;
import com.vela.im.service.friendship.application.dto.req.DeleteFriendShipGroupMemberReq;
import com.vela.im.shared.base.Result;

/**
 * @author wanqiu
 */
public interface ImFriendShipGroupMemberService {

    public Result addGroupMember(AddFriendShipGroupMemberReq req);

    public Result delGroupMember(DeleteFriendShipGroupMemberReq req);

    public int doAddGroupMember(Long groupId, String toId);

    public int clearGroupMember(Long groupId);

}
