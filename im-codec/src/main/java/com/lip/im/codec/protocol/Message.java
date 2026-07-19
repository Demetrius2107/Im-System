package com.lip.im.codec.protocol;

import lombok.Data;

/**
 * <p>Title: Message</p>
 * <p>Description: 消息协议体，包含消息头(MessageHeader)和消息体(messagePack)，是TCP/WebSocket协议的统一消息载体。</p>
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
public class Message {

    private MessageHeader messageHeader;

    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}