package com.vela.im.codec.pack.friendship;

import lombok.Data;

/**
 * <p>Title: 审批好友申请通知报文</p>
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
public class ApproverFriendRequestPack {

    /** 好友请求ID */
    private Long id;

    /** 审批状态：1-同意，2-拒绝 */
    private Integer status;

    /** 序列号 */
    private Long sequence;
}
