package com.vela.im.codec.pack.message;

import lombok.Data;

/**
 * <p>Title: ChatMessageAck</p>
 * <p>Description: 消息确认ACK包，服务端处理消息后向发送方返回的处理结果确认。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-24
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class ChatMessageAck {

    /**
     * 消息ID，用于客户端确认
     */
    private String messageId;

    /**
     * 消息序列号，用于排序和去重
     */
    private Long messageSequence;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageAck(String messageId, Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }

}
