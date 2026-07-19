package com.lip.im.shared.types.message;


import com.lip.im.shared.types.ClientInfo;
import lombok.Data;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class MessageReadedContent extends ClientInfo {

    private long messageSequence;

    private String fromId;

    private String groupId;

    private String toId;

    private Integer conversationType;

}
