package com.lip.im.codec.pack.message;

import lombok.Data;

/**
 * <p>Title: MessageReadedPack</p>
 * <p>Description: 消息已读回执包，通知发送方消息已被接收方已读。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2025-03-06
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class MessageReadedPack {

    private long messageSequence;

    private String fromId;

    private String groupId;

    private String toId;

    private Integer conversationType;
}
