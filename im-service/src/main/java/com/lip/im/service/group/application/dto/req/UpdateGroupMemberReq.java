package com.lip.im.service.group.application.dto.req;


import com.lip.im.shared.types.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author wanqiu
 * @description:
 **/
@Data
public class UpdateGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    @NotBlank(message = "memberId不能为空")
    private String memberId;

    private String alias;

    private Integer role;

    private String extra;

}
