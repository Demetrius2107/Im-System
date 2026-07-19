package com.lip.im.shared.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ClientInfo</p>
 * <p>Description: 客户端信息值对象，标识客户端的应用ID、端类型和设备唯一标识。</p>
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
@NoArgsConstructor
public class ClientInfo {

    /** 应用ID */
    private Integer appId;

    /** 客户端类型：1-iOS，2-Android，3-Windows，4-Mac，5-Web */
    private Integer clientType;

    /** 设备唯一标识IMEI */
    private String imei;

    public ClientInfo(Integer appId, Integer clientType, String imei) {
        this.appId = appId;
        this.clientType = clientType;
        this.imei = imei;
    }
}
