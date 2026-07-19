package com.vela.im.service.friendship.domain.service;


import com.vela.im.service.friendship.application.dto.req.AddFriendShipGroupMemberReq;
import com.vela.im.service.friendship.application.dto.req.DeleteFriendShipGroupMemberReq;
import com.vela.im.shared.base.ResponseVO;

/**
 * @author wanqiu
 */
public interface ImFriendShipGroupMemberService {

    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req);

    public ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req);

    public int doAddGroupMember(Long groupId, String toId);

    public int clearGroupMember(Long groupId);

}
