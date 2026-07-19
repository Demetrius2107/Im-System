package com.vela.im.codec.pack.group;

import lombok.Data;

/**
 * <p>Title: 修改群成员通知报文</p>
 * <p>Description: </p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */@Data
public class UpdateGroupMemberPack {

    /** 群组ID */
    private String groupId;

    /** 成员ID */
    private String memberId;

    /** 群昵称 */
    private String alias;

    /** 扩展字段 */
    private String extra;
}
