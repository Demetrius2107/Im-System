package com.vela.im.codec.pack.message;

import lombok.Data;

/**
 * <p>Title: MessageReciveServerAckPack</p>
 * <p>Description: 服务端消息接收确认包，当接收方不在线时由服务端代为确认。</p>
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
public class MessageReciveServerAckPack {

    /**
     * 消息Key，标识被确认的消息
     */
    private Long messageKey;

    /**
     * 发送方用户ID
     */
    private String fromId;

    /**
     * 接收方用户ID
     */
    private String toId;

    /**
     * 消息序列号
     */
    private Long messageSequence;

    /**
     * 是否为服务端代发确认：true-服务端发送，false-接收方发送
     */
    private Boolean serverSend;
}
