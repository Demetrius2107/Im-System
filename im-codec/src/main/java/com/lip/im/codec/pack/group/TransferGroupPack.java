package com.lip.im.codec.pack.group;

import lombok.Data;

/**
 * @author wanqiu
 * @description: 转让群主通知报文
 **/
@Data
public class TransferGroupPack {

    private String groupId;

    private String ownerId;

}
