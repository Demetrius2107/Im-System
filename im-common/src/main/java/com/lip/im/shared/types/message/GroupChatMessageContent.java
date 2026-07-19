package com.lip.im.shared.types.message;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class GroupChatMessageContent extends MessageContent {

    private String groupId;

    private List<String> memberId;

}
