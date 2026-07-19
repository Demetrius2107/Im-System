package com.lip.im.codec.pack.group;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: 群内添加群成员通知报文</p>
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
public class AddGroupMemberPack {

    private String groupId;

    private List<String> members;

}
