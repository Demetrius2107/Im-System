package com.lip.im.imservice.user.model;

import com.hua.im.imcommon.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Shukun.Li
 * @description: status 区分是上线还是下线
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserStatusChangeNotifyContent extends ClientInfo {

    private String userId;

    // 服务端状态 1 上线 2 离线
    private Integer status;
}
