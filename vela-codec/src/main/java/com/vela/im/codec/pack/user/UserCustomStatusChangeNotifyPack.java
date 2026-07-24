package com.vela.im.codec.pack.user;

import lombok.Data;

/**
 * <p>Title: UserCustomStatusChangeNotifyPack</p>
 * <p>Description: 用户自定义状态变更通知包，当用户修改自定义状态时广播给好友和订阅者。</p>
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
public class UserCustomStatusChangeNotifyPack {

    /**
     * 自定义状态文本
     */
    private String customText;

    /**
     * 自定义状态码
     */
    private Integer customStatus;

    /**
     * 用户ID
     */
    private String userId;

}
