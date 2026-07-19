package com.lip.im.service.group.application.dto.req;


import com.lip.im.shared.types.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author wanqiu
 * @description:
 **/
@Data
public class RemoveGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    private String memberId;

}
