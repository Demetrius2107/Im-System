package com.vela.im.codec.pack.group;

import lombok.Data;

/**
 * <p>Title: 群聊消息分发报文
 * </p>
 * <p>Description: </p>
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
public class GroupMessagePack {

    /**
     * 客户端传的消息ID，用于去重
     */
    private String messageId;

    /**
     * 消息唯一标识Key
     */
    private String messageKey;

    /**
     * 发送方用户ID
     */
    private String fromId;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 消息随机码，用于去重
     */
    private int messageRandom;

    /**
     * 消息发送时间戳
     */
    private long messageTime;

    /**
     * 消息序列号
     */
    private long messageSequence;

    /**
     * 消息体内容
     */
    private String messageBody;

    /**
     * 角标模式：0-需要计数，1-不增加计数
     */
    private int badgeMode;

    /**
     * 消息有效期（毫秒）
     */
    private Long messageLifeTime;

    /**
     * 应用ID
     */
    private Integer appId;

}
