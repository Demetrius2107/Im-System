package com.lip.im.shared.types;

import lombok.Data;

/**
 * <p>Title: UserSession</p>
 * <p>Description: 用户会话值对象，存储用户在Redis中的会话信息，包括连接状态、网关节点等。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class UserSession {

    /** 用户ID */
    private String userId;

    /** 应用ID */
    private Integer appId;

    /** 客户端类型 */
    private Integer clientType;

    /** 设备唯一标识IMEI */
    private String imei;

    /** 连接状态：0-离线，1-在线 */
    private Integer connectState;

    /** 网关节点ID */
    private Integer brokerId;

    /** 网关节点主机地址 */
    private String brokerHost;

    /** SDK版本号 */
    private Integer version;

}
