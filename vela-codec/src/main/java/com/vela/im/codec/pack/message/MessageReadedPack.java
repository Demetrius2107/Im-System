package com.vela.im.codec.pack.message;

import lombok.Data;

/**
 * <p>Title: MessageReadedPack</p>
 * <p>Description: 消息已读回执包，通知发送方消息已被接收方已读。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class MessageReadedPack {

    /**
     * 消息序列号
     */
    private long messageSequence;

    /**
     * 发送方用户ID
     */
    private String fromId;

    /**
     * 群组ID（群聊已读时使用）
     */
    private String groupId;

    /**
     * 接收方用户ID
     */
    private String toId;

    /**
     * 会话类型：1-单聊 2-群聊
     */
    private Integer conversationType;
}
