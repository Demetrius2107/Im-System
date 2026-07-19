package com.lip.im.shared.types.message;

import lombok.Data;


/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class CheckSendMessageReq {

    private String fromId;

    private String toId;

    private Integer appId;

    private Integer command;

}
