package com.vela.im.shared.types;

import lombok.Data;

/**
 * <p>Title: UserClientDto</p>
 * <p>Description: 用户客户端数据传输对象，标识用户在某端设备上的登录信息。</p>
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
public class UserClientDto {

    /** 应用ID */
    private Integer appId;

    /** 客户端类型 */
    private Integer clientType;

    /** 用户ID */
    private String userId;

    /** 设备唯一标识IMEI */
    private String imei;

}
