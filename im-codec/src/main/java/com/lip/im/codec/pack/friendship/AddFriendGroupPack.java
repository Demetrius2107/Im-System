package com.lip.im.codec.pack.friendship;

import lombok.Data;

/**
 * <p>Title: 用户创建好友分组通知包</p>
 * <p>Description: </p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */@Data
public class AddFriendGroupPack {
    /** 发起方用户ID */
    public String fromId;

    /** 分组名称 */
    private String groupName;

    /** 序列号 */
    private Long sequence;
}
