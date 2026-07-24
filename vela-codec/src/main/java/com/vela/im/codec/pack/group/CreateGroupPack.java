package com.vela.im.codec.pack.group;

import lombok.Data;

/**
 * <p>Title: 创建群组通知报文
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
public class CreateGroupPack {

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 群主ID
     */
    private String ownerId;

    /**
     * 群类型：1-私有群（类似微信），2-公开群（类似QQ）
     */
    private Integer groupType;

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 是否全员禁言：0-不禁言，1-全员禁言
     */
    private Integer mute;

    /**
     * 申请加群选项：0-禁止任何人申请，1-需要审批，2-允许自由加入
     */
    private Integer applyJoinType;

    /**
     * 是否禁止私聊：0-允许私聊，1-禁止私聊
     */
    private Integer privateChat;

    /**
     * 群简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String notification;

    /**
     * 群头像URL
     */
    private String photo;

    /**
     * 群状态：0-正常，1-解散
     */
    private Integer status;

    /**
     * 序列号
     */
    private Long sequence;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 扩展字段
     */
    private String extra;

}
