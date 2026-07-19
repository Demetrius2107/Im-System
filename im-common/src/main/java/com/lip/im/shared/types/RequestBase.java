package com.lip.im.shared.types;

import lombok.Data;

/**
 * <p>Title: RequestBase</p>
 * <p>Description: 请求基础类，所有API请求的基类，包含应用ID、操作人、端类型和设备标识。</p>
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
public class RequestBase {

    /** 应用ID */
    private Integer appId;

    /** 操作人用户ID */
    private String operater;

    /** 客户端类型 */
    private Integer clientType;

    /** 设备唯一标识IMEI */
    private String imei;
}
