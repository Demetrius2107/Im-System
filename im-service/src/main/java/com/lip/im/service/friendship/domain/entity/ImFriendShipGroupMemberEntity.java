package com.lip.im.service.friendship.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Shukun.Li
 */
/**
 * <p>Title: ImFriendShipGroupMemberEntity</p>
 * <p>Description: 好友分组成员领域实体，映射 im_friendship_group_member 表</p>
 * <p>项目名称: IM-System</p>
 *
 * @author lld
 * @since 1.0
 * @createTime 2025-03-06
 * @updateTime 2025-03-06
 *
 * Copyright © 2025 lld All rights reserved
 */
@Data
@TableName("im_friendship_group_member")
public class ImFriendShipGroupMemberEntity {

    @TableId(value = "group_id")
    private Long groupId;

    private String toId;
}
