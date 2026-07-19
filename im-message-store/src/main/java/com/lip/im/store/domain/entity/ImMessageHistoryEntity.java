package com.lip.im.store.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wanqiu
 * @description:
 **/
/**
 * <p>Title: ImMessageHistoryEntity</p>
 * <p>Description: 单聊消息历史持久化实体，映射 im_message_history 表</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2025-03-03
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
@TableName("im_message_history")
public class ImMessageHistoryEntity {

    private Integer appId;

    private String fromId;

    private String toId;

    private String ownerId;

    /** messageBodyId*/
    private Long messageKey;
    /** 序列号*/
    private Long sequence;

    private String messageRandom;

    private Long messageTime;

    private Long createTime;

}
