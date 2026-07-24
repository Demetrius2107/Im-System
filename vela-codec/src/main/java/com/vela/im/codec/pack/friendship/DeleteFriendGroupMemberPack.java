package com.vela.im.codec.pack.friendship;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: 删除好友分组成员通知报文
 * </p>
 * <p>Description: </p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 * <p>
 * Copyright © 2026 wanqiu All rights reserved
 * @since 1.0
 */
@Data
public class DeleteFriendGroupMemberPack {

    /**
     * 发起方用户ID
     */
    public String fromId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 移除的成员用户ID列表
     */
    private List<String> toIds;

    /**
     * 序列号
     */
    private Long sequence;
}
