package com.lip.im.codec.pack.conversation;

import lombok.Data;

/**
 * <p>Title: DeleteConversationPack</p>
 * <p>Description: 删除会话消息包，通知对端删除指定会话。</p>
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
public class DeleteConversationPack {

    /** 会话ID，格式: type_fromId_toId */
    private String conversationId;

}
