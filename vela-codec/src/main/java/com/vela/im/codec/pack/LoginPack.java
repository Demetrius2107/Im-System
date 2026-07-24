package com.vela.im.codec.pack;

import lombok.Data;

/**
 * <p>Title: LoginPack</p>
 * <p>Description: 登录消息包，客户端登录时携带 userId。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 * <p>
 * Copyright © 2026 wanqiu All rights reserved
 * @since 1.0
 */
@Data
public class LoginPack {

    /**
     * 用户ID
     */
    private String userId;

}
