package com.vela.im.service.user.application.dto.req;


import com.vela.im.shared.types.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wanqiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserId extends RequestBase {

    private String userId;
}
