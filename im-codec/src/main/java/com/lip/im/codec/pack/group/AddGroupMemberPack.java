package com.lip.im.codec.pack.group;

import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 * @description: 群内添加群成员通知报文
 **/
@Data
public class AddGroupMemberPack {

    private String groupId;

    private List<String> members;

}
