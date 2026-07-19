package com.lip.im.service.friendship.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.AutoMap;
import lombok.Data;

/**
 * <p>Title: ImFriendShipEntity</p>
 * <p>Description: 好友关系领域实体，映射 im_friendship 表</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-06
 * @updateTime 2025-03-06
 *
 * Copyright © 2025 Shukun.Li All rights reserved
 */
@Data
@TableName("im_friendship")
@AutoMap
public class ImFriendShipEntity {

    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 发送方id
     */
    @TableField(value = "from_id")
    private String fromId;

    /**
     * 接受方id
     */
    @TableField(value = "to_id")
    private String toId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态 1正常 2删除
     */
    private Integer status;

    /**
     * 状态 1正常 2拉黑
     */
    private Integer black;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 好友关系序列号
     */
    private Long friendSequence;

    /**
     * 黑名单关系序列号
     */
    private Long blackSequence;

    /**
     * 好友来源
     */
    private String addSource;

    /**
     * 拓展
     */
    private String extra;
}
