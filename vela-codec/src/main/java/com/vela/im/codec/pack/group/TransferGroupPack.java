package com.vela.im.codec.pack.group;

import lombok.Data;

/**
 * <p>Title: 转让群主通知报文
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
public class TransferGroupPack {

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 新群主ID
     */
    private String ownerId;

}
