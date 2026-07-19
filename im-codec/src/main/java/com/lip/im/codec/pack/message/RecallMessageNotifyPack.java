package com.lip.im.codec.pack.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: 撤回消息通知报文</p>
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
@NoArgsConstructor
public class RecallMessageNotifyPack {

    /** 发送方用户ID */
    private String fromId;

    /** 接收方用户ID */
    private String toId;

    /** 消息Key，标识被撤回的消息 */
    private Long messageKey;

    /** 消息序列号 */
    private Long messageSequence;
}
