package com.vela.im.codec.protocol;

import lombok.Data;

/**
 * <p>Title: Message</p>
 * <p>Description: 消息协议体，包含消息头(MessageHeader)和消息体(messagePack)，是TCP/WebSocket协议的统一消息载体。</p>
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
public class Message {

    /**
     * 消息协议头，包含指令/版本/端类型等元数据
     *
     */
    private MessageHeader messageHeader;

    /**
     * 消息体，不同类型消息对应不同数据结构（JSON/PB等）
     *
     */
    private Object messagePackage;

    @Override
    public String toString() {
        return "Message{" + "messageHeader=" + messageHeader + ", messagePack=" + messagePackage + '}';
    }
}