package com.lip.im.codec.pack.user;


import com.lip.im.shared.types.UserSession;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: UserStatusChangeNotifyPack</p>
 * <p>Description: 用户在线状态变更通知包，当用户上线/下线时广播给好友和订阅者。</p>
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
public class UserStatusChangeNotifyPack {

    private Integer appId;

    private String userId;

    private Integer status;

    private List<UserSession> client;

}
