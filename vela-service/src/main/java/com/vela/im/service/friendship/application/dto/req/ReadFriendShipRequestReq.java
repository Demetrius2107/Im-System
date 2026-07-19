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
public class ReadFriendShipRequestReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;
}
