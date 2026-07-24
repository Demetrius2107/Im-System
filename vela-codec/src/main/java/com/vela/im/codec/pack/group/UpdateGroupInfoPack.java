package com.vela.im.codec.pack.group;

import lombok.Data;

/**
 * <p>Title: 修改群信息通知报文
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
public class UpdateGroupInfoPack {

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 是否全员禁言：0-不禁言，1-全员禁言
     */
    private Integer mute;

    /**
     * 加入群权限：0-所有人可加入，1-群成员可拉人，2-管理员可拉人
     */
    private Integer joinType;

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
     * 群成员上限
     */
    private Integer maxMemberCount;

    /**
     * 序列号
     */
    private Long sequence;
}
