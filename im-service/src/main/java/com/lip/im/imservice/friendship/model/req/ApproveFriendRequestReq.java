package com.lip.im.imservice.friendship.model.req;


import com.lip.im.model.model.RequestBase;
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
