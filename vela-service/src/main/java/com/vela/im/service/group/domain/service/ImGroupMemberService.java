package com.vela.im.service.group.domain.service;



import com.vela.im.service.group.application.dto.req.*;
import com.vela.im.service.group.application.dto.resp.GetRoleInGroupResp;
import com.vela.im.shared.base.Result;

import java.util.Collection;
import java.util.List;

/**
 * 群组成员管理领域服务接口，定义群组成员的导入、添加、移除、查询、角色查询、禁言等核心业务能力。
 *
 * @author wanqiu
 * @version 1.1
 * @since 1.0
 */
public interface ImGroupMemberService {

    /**
     * 批量导入群成员（初始化/历史数据迁移场景使用）
     *
     * @param importGroupMemberRequest 导入群成员请求体，包含群组 ID、应用 ID 及成员列表
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> importGroupMember(ImportGroupMemberRequest importGroupMemberRequest);

    /**
     * 添加群成员，支持私有群成员直接拉人入群及后台管理员直接入群，入群后发送加群 MQ 消息
     *
     * @param addGroupMemberRequest 添加群成员请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> addMember(AddGroupMemberRequest addGroupMemberRequest);

    /**
     * 移除群成员，校验操作者权限后执行踢人操作，并发送退群 MQ 消息
     *
     * @param removeGroupMemberRequest 移除群成员请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> removeMember(RemoveGroupMemberRequest removeGroupMemberRequest);

    /**
     * 添加单个群成员（内部调用），校验用户有效性及群主唯一性后插入或更新成员记录
     *
     * @param groupId 群组 ID
     * @param appId   应用 ID
     * @param dto     群成员信息 DTO
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> addGroupMember(String groupId, Integer appId, GroupMemberDto dto);

    /**
     * 移除单个群成员（内部调用），将成员角色标记为离开状态并记录离开时间
     *
     * @param groupId  群组 ID
     * @param appId    应用 ID
     * @param memberId 成员 ID
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> removeGroupMember(String groupId, Integer appId, String memberId);

    /**
     * 查询指定成员在群组中的角色及禁言时间等信息
     *
     * @param groupId  群组 ID
     * @param memberId 成员 ID
     * @param appId    应用 ID
     * @return 成员在群内的角色信息，若未入群返回失败结果
     */
    public Result<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId);

    /**
     * 获取指定成员已加入的群组 ID 列表（支持分页）
     *
     * @param getJoinedGroupRequest 获取已加入群组请求体
     * @return 已加入群组 ID 集合
     */
    public Result<Collection<String>> getMemberJoinedGroup(GetJoinedGroupRequest getJoinedGroupRequest);

    /**
     * 获取指定群组的所有成员信息列表
     *
     * @param groupId 群组 ID
     * @param appId   应用 ID
     * @return 群成员信息列表
     */
    public Result<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId);

    /**
     * 获取指定群组的所有成员 ID 列表
     *
     * @param groupId 群组 ID
     * @param appId   应用 ID
     * @return 成员 ID 列表
     */
    public List<String> getGroupMemberId(String groupId, Integer appId);

    /**
     * 获取指定群组的管理员列表（不包含群主和普通成员）
     *
     * @param groupId 群组 ID
     * @param appId   应用 ID
     * @return 群管理员信息列表
     */
    public List<GroupMemberDto> getGroupManager(String groupId, Integer appId);

    /**
     * 更新群成员信息，支持修改群昵称、成员角色等，并发送更新成员 MQ 消息
     *
     * @param updateGroupMemberRequest 更新群成员请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> updateGroupMember(UpdateGroupMemberRequest updateGroupMemberRequest);

    /**
     * 转让群主，将原群主降级为普通成员，并将指定成员提升为新群主
     *
     * @param owner   新群主 ID
     * @param groupId 群组 ID
     * @param appId   应用 ID
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> transferGroupMember(String owner, String groupId, Integer appId);

    /**
     * 设置群成员禁言时间，校验操作者权限后更新成员的禁言截止日期，并发送禁言 MQ 消息
     *
     * @param speakMemberRequest 禁言成员请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> speak(SpeakMemberRequest speakMemberRequest);

    /**
     * 增量同步操作者已加入的群组 ID 列表（排除已离开群组）
     *
     * @param operater 操作者用户 ID
     * @param appId    应用 ID
     * @return 已加入群组 ID 集合
     */
    Result<Collection<String>> syncMemberJoinedGroup(String operater, Integer appId);
}
