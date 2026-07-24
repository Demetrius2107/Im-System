package com.vela.im.codec.pack.user;

import lombok.Data;

/**
 * <p>Title: UserModifyPack</p>
 * <p>Description: 用户资料变更通知包，当用户资料修改后广播给所有在线端。</p>
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
public class UserModifyPack {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像URL
     */
    private String photo;

    /**
     * 性别
     */
    private String userSex;

    /**
     * 个性签名
     */
    private String selfSignature;

    /**
     * 加好友验证类型：1-需要验证
     */
    private Integer friendAllowType;

}
