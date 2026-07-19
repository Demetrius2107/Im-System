package com.lip.im.codec.pack.group;

import lombok.Data;

/**
 * @author wanqiu
 * @description: 群成员禁言通知报文
 **/
@Data
public class GroupMemberSpeakPack {

    private String groupId;

    private String memberId;

    private Long speakDate;

}
