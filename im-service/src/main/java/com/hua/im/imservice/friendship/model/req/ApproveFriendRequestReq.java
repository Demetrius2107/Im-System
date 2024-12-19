package com.hua.im.imservice.friendship.model.req;

import com.hua.im.imcommon.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Shukun.Li
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApproveFriendRequestReq extends RequestBase {

    private Long id;

    // 1 同意 2 拒绝
    private Integer status;
}
