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
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class UpdateConversationPack {

    /** 会话ID */
    private String conversationId;

    /** 是否免打扰：0-否，1-是 */
    private Integer isMute;

    /** 是否置顶：0-否，1-是 */
    private Integer isTop;

    /** 会话类型：0-单聊，1-群聊 */
    private Integer conversationType;

    /** 会话序列号 */
    private Long sequence;

}
