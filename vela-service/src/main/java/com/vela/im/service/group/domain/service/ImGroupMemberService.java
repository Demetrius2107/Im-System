package com.vela.im.service.group.domain.service;



import com.vela.im.service.group.application.dto.req.*;
import com.vela.im.service.group.application.dto.resp.GetRoleInGroupResp;
import com.vela.im.shared.base.Result;

import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
public interface ImGroupMemberService {

    public Result importGroupMember(ImportGroupMemberReq req);

    public Result addMember(AddGroupMemberReq req);

    public Result removeMember(RemoveGroupMemberReq req);

    public Result addGroupMember(String groupId, Integer appId, GroupMemberDto dto);

    public Result removeGroupMember(String groupId, Integer appId, String memberId);

    public Result<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId);

    public Result<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req);

    public Result<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId);

    public List<String> getGroupMemberId(String groupId, Integer appId);

    public List<GroupMemberDto> getGroupManager(String groupId, Integer appId);

    public Result updateGroupMember(UpdateGroupMemberReq req);

    public Result transferGroupMember(String owner, String groupId, Integer appId);

    public Result speak(SpeaMemberReq req);

    Result<Collection<String>> syncMemberJoinedGroup(String operater, Integer appId);
}
