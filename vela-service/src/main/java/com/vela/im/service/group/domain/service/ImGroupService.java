package com.vela.im.service.group.domain.service;


import com.vela.im.service.group.domain.entity.ImGroupEntity;
import com.vela.im.service.group.application.dto.req.*;
import com.vela.im.service.group.application.dto.resp.GetGroupResp;
import com.vela.im.service.group.application.dto.resp.GetJoinedGroupResp;
import com.vela.im.shared.base.Result;
import com.vela.im.shared.types.SyncReq;
import com.vela.im.shared.types.SyncResp;

/**
 * 群组管理领域服务接口，定义群组的创建、导入、更新、解散、转让、禁言等核心业务能力。
 *
 * @author wanqiu
 * @version 1.1
 * @since 1.0
 */
public interface ImGroupService {

    /**
     * 导入群组（批量/初始化场景使用）
     *
     * @param importGroupRequest 导入群组请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> importGroup(ImportGroupRequest importGroupRequest);

    /**
     * 创建群组，自动生成群主和群成员，并发送创建群组 MQ 消息
     *
     * @param createGroupRequest 创建群组请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> createGroup(CreateGroupRequest createGroupRequest);

    /**
     * 更新群组基础信息，校验操作者权限后执行更新，并发送更新群组 MQ 消息
     *
     * @param updateGroupRequest 更新群组请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> updateBaseGroupInfo(UpdateGroupRequest updateGroupRequest);

    /**
     * 获取用户已加入的群组列表
     *
     * @param getJoinedGroupRequest 获取已加入群组请求体
     * @return 已加入群组列表，包含总数和群组详情
     */
    public Result<GetJoinedGroupResp> getJoinedGroup(GetJoinedGroupRequest getJoinedGroupRequest);

    /**
     * 解散群组，校验操作者权限后更新群组状态为解散，并发送解散群组 MQ 消息
     *
     * @param destroyGroupRequest 解散群组请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> destroyGroup(DestroyGroupRequest destroyGroupRequest);

    /**
     * 转让群组，校验原群主身份后更新群主 ID，并同步更新群成员角色
     *
     * @param transferGroupRequest 转让群组请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> transferGroup(TransferGroupRequest transferGroupRequest);

    /**
     * 根据群 ID 和应用 ID 获取单个群组实体
     *
     * @param groupId 群组 ID
     * @param appId   应用 ID
     * @return 群组实体，若不存在返回失败结果
     */
    public Result<ImGroupEntity> getGroup(String groupId, Integer appId);

    /**
     * 获取群组详情（含成员列表）
     *
     * @param getGroupRequest 获取群组详情请求体
     * @return 群组详情，包含基本信息及成员列表
     */
    public Result<GetGroupResp> getGroup(GetGroupRequest getGroupRequest);

    /**
     * 设置群组全员禁言，校验操作者权限后执行更新
     *
     * @param muteGroupRequest 禁言设置请求体
     * @return 无数据返回，仅表示成功或失败
     */
    public Result<Void> muteGroup(MuteGroupRequest muteGroupRequest);

    /**
     * 增量同步用户已加入的群组列表（基于最大 sequence）
     *
     * @param syncRequest 同步请求体，包含用户信息及上次同步的 sequence
     * @return 同步结果，包含增量群组列表及最大 sequence
     */
    Result<SyncResp<ImGroupEntity>> syncJoinedGroupList(SyncReq syncRequest);

    /**
     * 获取用户在群组模块的最大 sequence，用于同步断点
     *
     * @param userId 用户 ID
     * @param appId  应用 ID
     * @return 最大 sequence 值
     */
    Long getUserGroupMaxSeq(String userId, Integer appId);
}
