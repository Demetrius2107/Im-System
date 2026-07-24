package com.vela.im.codec.pack.friendship;

import lombok.Data;

/**
 * <p>Title: 已读好友申请通知报文
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
public class ReadAllFriendRequestPack {

    /**
     * 用户ID，标识已读所有好友申请的用户
     */
    private String fromId;

    /**
     * 序列号
     */
    private Long sequence;
}
