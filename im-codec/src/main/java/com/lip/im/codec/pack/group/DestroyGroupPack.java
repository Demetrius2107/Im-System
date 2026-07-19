package com.lip.im.codec.pack.group;

import lombok.Data;

/**
 * @author wanqiu
 * @description: 解散群通知报文
 **/
@Data
public class DestroyGroupPack {

    private String groupId;

    private Long sequence;

}
