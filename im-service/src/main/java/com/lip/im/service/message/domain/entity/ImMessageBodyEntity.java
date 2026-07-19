package com.lip.im.service.message.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wanqiu
 * @description:
 **/
/**
 * <p>Title: ImMessageBodyEntity</p>
 * <p>Description: 消息体领域实体，映射 im_message_body 表</p>
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
@TableName("im_message_body")
public class ImMessageBodyEntity {

    private Integer appId;

    /** messageBodyId*/
    private Long messageKey;

    /** messageBody*/
    private String messageBody;

    private String securityKey;

    private Long messageTime;

    private Long createTime;

    private String extra;

    private Integer delFlag;

}
