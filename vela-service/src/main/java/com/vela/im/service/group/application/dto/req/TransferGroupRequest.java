package com.vela.im.service.group.application.dto.req;


import com.vela.im.shared.types.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class TransferGroupRequest extends RequestBase {

    @NotNull(message = "群id不能为空")
    private String groupId;

    private String ownerId;

}
