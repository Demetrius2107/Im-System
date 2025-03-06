package com.lip.im.imservice.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Shukun.Li
 */
@Data
@TableName("im_friendship_group")
public class ImFriendShipGroupEntity {

    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    private String fromId;

    private Integer appId;

    private String groupName;

    private Long createTime;

    private Long updateTime;

    /**
     * 序列号
     */
    private Long sequence;

    private int delFlag;
}
