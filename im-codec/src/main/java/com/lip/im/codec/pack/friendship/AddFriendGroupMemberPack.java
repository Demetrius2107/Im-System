package com.lip.im.codec.pack.friendship;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: 好友分组添加成员通知包</p>
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
public class AddFriendGroupMemberPack {

    public String fromId;

    private String groupName;

    private List<String> toIds;

    /** 序列号*/
    private Long sequence;
}
