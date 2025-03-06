package com.lip.im.imservice.friendship.model.req;


import com.lip.im.model.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author Shukun.Li
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetAllFriendShipReq extends RequestBase {

    @NotBlank(message = "用户id不为空")
    private String fromId;
}
