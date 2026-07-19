package com.lip.im.codec.pack.message;

import lombok.Data;

/**
 * <p>Title: MessageReciveServerAckPack</p>
 * <p>Description: 服务端消息接收确认包，当接收方不在线时由服务端代为确认。</p>
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
public class MessageReciveServerAckPack {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    private Boolean serverSend;
}
