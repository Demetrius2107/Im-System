package com.lip.im.codec.pack.friendship;

import lombok.Data;

/**
 * <p>Title: 已读好友申请通知报文</p>
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
public class ReadAllFriendRequestPack {

    private String fromId;

    private Long sequence;
}
