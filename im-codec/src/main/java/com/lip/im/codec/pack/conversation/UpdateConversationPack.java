package com.lip.im.codec.pack.conversation;

import lombok.Data;

/**
 * <p>Title: UpdateConversationPack</p>
 * <p>Description: 更新会话消息包，通知对端更新会话置顶/免打扰状态。</p>
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
public class UpdateConversationPack {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private Integer conversationType;

    private Long sequence;

}
