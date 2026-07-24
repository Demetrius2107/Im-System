package com.vela.im.codec.pack.user;

import lombok.Data;

/**
 * <p>Title: LoginAckPack</p>
 * <p>Description: 登录响应包，服务端处理登录请求后向客户端返回的登录结果。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-24
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class LoginAckPack {

    /**
     * 用户ID
     */
    private String userId;

}
