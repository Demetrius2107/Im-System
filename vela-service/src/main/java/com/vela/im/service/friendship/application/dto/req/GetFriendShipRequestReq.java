package com.vela.im.service.friendship.application.dto.req;


import com.vela.im.shared.types.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author wanqiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetFriendShipRequestReq extends RequestBase {

    @NotBlank(message = "用户id不为空")
    private String formId;
}
