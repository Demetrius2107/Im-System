package com.hua.im.imcommon.model;

import lombok.Data;

/**
 * @author Shukun.Li
 */
@Data
public class RequestBase {

    private Integer appId;

    private String operator;

    private Integer clientType;

    private String imei;
}
