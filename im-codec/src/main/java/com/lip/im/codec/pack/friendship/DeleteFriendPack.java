package com.lip.im.codec.pack.friendship;

import lombok.Data;

/**
 * @author wanqiu
 * @description: 删除好友通知报文
 **/
@Data
public class DeleteFriendPack {

    private String fromId;

    private String toId;

    private Long sequence;
}
