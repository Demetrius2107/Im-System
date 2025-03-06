package com.lip.im.imservice.group.model.req;


import com.lip.im.model.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class DestroyGroupReq extends RequestBase {

    @NotNull(message = "群id不能为空")
    private String groupId;

}
