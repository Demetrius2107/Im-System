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
 * @updateTime 2025-03-06
 *
 * Copyright © 2026 wanqiu All rights reserved
 */@Data
public class DestroyGroupPack {

    private String groupId;

    private Long sequence;

}
