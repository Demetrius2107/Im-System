package com.vela.im.codec.pack.friendship;

import lombok.Data;

/**
 * <p>Title: 删除好友通知报文
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
public class DeleteFriendPack {

    /**
     * 发起方用户ID
     */
    private String fromId;

    /**
     * 被删除好友用户ID
     */
    private String toId;

    /**
     * 序列号
     */
    private Long sequence;
}
