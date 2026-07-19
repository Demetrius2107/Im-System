package com.vela.im.store.application.dto;


import com.vela.im.store.domain.entity.ImMessageBodyEntity;
import com.vela.im.shared.types.message.MessageContent;
import lombok.Data;

/**
 * <p>Title: DoStoreP2PMessageDto</p>
 * <p>Description: 单聊消息存储DTO，封装消息内容和消息体实体，用于MQ异步存储。</p>
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
public class DoStoreP2PMessageDto {

    /** 消息内容 */
    private MessageContent messageContent;

    /** 消息体实体 */
    private ImMessageBodyEntity imMessageBodyEntity;

}
