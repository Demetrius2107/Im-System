package com.lip.im.codec.pack.group;

import lombok.Data;

/**
 * <p>Title: 解散群通知报文</p>
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
public class DestroyGroupPack {

    /** 群组ID */
    private String groupId;

    /** 序列号 */
    private Long sequence;

}
