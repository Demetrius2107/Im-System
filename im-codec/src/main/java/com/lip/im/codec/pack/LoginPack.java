package com.lip.im.codec.pack;

import lombok.Data;

/**
 * <p>Title: LoginPack</p>
 * <p>Description: 登录消息包，客户端登录时携带 userId。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2025-03-06
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class LoginPack {

    private String userId;

}
