package com.lip.im.service.conversation.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>Title: ImConversationSetEntity</p>
 * <p>Description: 会话领域实体，映射 im_conversation_set 表，管理会话置顶/免打扰/已读状态</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-06
 * @updateTime 2025-03-06
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
@TableName("im_conversation_set")
public class ImConversationSetEntity {

    //会话id 0_fromId_toId
    private String conversationId;

    //会话类型
    private Integer conversationType;

    private String fromId;

    private String toId;

    private int isMute;

    private int isTop;

    private Long sequence;

    private Long readedSequence;

    private Integer appId;
}
