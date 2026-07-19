package com.vela.im.codec.pack.friendship;

import lombok.Data;

/**
 * <p>Title: 用户添加黑名单以后tcp通知数据包</p>
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
public class AddFriendBlackPack {
    /** 发起方用户ID */
    private String fromId;

    /** 被拉黑用户ID */
    private String toId;

    /** 序列号 */
    private Long sequence;
}
