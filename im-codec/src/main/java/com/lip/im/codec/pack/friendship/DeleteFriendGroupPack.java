package com.lip.im.codec.pack.friendship;

import lombok.Data;

/**
 * @author wanqiu
 * @description: 删除好友分组通知报文
 **/
@Data
public class DeleteFriendGroupPack {
    public String fromId;

    private String groupName;

    /** 序列号*/
    private Long sequence;
}
