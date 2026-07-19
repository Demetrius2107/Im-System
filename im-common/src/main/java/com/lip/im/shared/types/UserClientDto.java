package com.lip.im.shared.types;

import lombok.Data;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class UserClientDto {

    private Integer appId;

    private Integer clientType;

    private String userId;

    private String imei;

}
