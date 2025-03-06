package com.lip.im.model.model;

import lombok.Data;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class RequestBase {
    private Integer appId;

    private String operater;

    private Integer clientType;

    private String imei;
}
