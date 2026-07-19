package com.vela.im.service.friendship.application.dto.req;


import com.vela.im.shared.types.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wanqiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApproveFriendRequestReq extends RequestBase {

    private Long id;

    // 1 同意 2 拒绝
    private Integer status;
}
