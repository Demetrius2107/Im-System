package com.lip.im.codec.pack.user;


import com.lip.im.shared.types.UserSession;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class UserStatusChangeNotifyPack {

    private Integer appId;

    private String userId;

    private Integer status;

    private List<UserSession> client;

}
