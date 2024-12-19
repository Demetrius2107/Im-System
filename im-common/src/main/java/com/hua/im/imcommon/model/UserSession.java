package com.hua.im.imcommon.model;

/**
 * @author Shukun.Li
 */
public class UserSession {

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 端的标识
     */
    private Integer clientType;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 连接状态 1=在线 2=离线
     */
    private Integer connectState;

    private Integer brokerId;

    private String brokerHost;

    private String imei;
}
