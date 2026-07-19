package com.lip.im.codec.pack.friendship;

import lombok.Data;

/**
 * <p>Title: 添加好友通知报文</p>
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
public class AddFriendPack {
    /** 发起方用户ID */
    private String fromId;

    /** 备注 */
    private String remark;

    /** 接收方用户ID */
    private String toId;

    /** 好友来源 */
    private String addSource;

    /** 添加好友时的描述信息 */
    private String addWording;

    /** 序列号 */
    private Long sequence;
}
