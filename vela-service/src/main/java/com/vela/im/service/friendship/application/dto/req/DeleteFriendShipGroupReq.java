package com.vela.im.service.friendship.application.dto.req;


import com.vela.im.shared.types.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wanqiu
 * @description: 删除分组，同时删除分组下的成员
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteFriendShipGroupReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotEmpty(message = "分组名称不能为空")
    private List<String> groupName;

}
