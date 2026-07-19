package com.lip.im.shared.types.message;

import com.lip.im.shared.types.ClientInfo;
import lombok.Data;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class MessageReceiveAckContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;


}
