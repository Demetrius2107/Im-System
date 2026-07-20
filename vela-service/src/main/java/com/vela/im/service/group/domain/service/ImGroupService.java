package com.vela.im.service.group.domain.service;


import com.vela.im.service.group.domain.entity.ImGroupEntity;
import com.vela.im.service.group.application.dto.req.*;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.types.SyncReq;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
public interface ImGroupService {

    public Result importGroup(ImportGroupReq req);

    public Result createGroup(CreateGroupReq req);

    public Result updateBaseGroupInfo(UpdateGroupReq req);

    public Result getJoinedGroup(GetJoinedGroupReq req);

    public Result destroyGroup(DestroyGroupReq req);

    public Result transferGroup(TransferGroupReq req);

    public Result<ImGroupEntity> getGroup(String groupId, Integer appId);

    public Result getGroup(GetGroupReq req);

    public Result muteGroup(MuteGroupReq req);

    Result syncJoinedGroupList(SyncReq req);

    Long getUserGroupMaxSeq(String userId, Integer appId);
}
