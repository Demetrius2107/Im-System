package com.lip.im.service.friendship.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Shukun.Li
 */
/**
 * <p>Title: ImFriendShipRequestEntity</p>
 * <p>Description: 好友请求领域实体，映射 im_friendship_request 表</p>
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
@TableName("im_friendship_request")
public class ImFriendShipRequestEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer appId;

    private String fromId;

    private String toId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否已读 1已读
     */
    private Integer readStatus;

    /**
     * 好友来源
     */
    private String addSource;

    private String addWording;

    /**
     * 审批状态 1同意 2拒绝
     */
    private Integer approveStatus;

    private Long createTime;

    private Long updateTime;

    /**
     * 序列号
     */
    private Long sequence;
}
