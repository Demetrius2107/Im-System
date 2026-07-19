package com.lip.im.shared.types.message;


import com.lip.im.shared.types.ClientInfo;
import lombok.Data;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class MessageContent extends ClientInfo {

    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

    private Long messageTime;

    private String extra;

    private Long messageKey;

    private long messageSequence;

}
