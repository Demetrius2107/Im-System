package com.lip.im.codec.pack.friendship;

import lombok.Data;


/**
 * <p>Title: 修改好友通知报文</p>
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
public class UpdateFriendPack {

    /** 发起方用户ID */
    public String fromId;

    /** 被修改的好友用户ID */
    private String toId;

    /** 备注 */
    private String remark;

    /** 序列号 */
    private Long sequence;
}
