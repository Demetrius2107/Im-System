package com.lip.im.imservice.friendship.model.req;

import com.hua.im.imcommon.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Shukun.Li
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CheckFriendShipReq  extends RequestBase {

    /**
     * 发送方id
     */
    @NotBlank(message = "fromId不能为空")
    private String fromId;

    /**
     * 接收方Id
     */
    @NotEmpty (message = "toIds不能为空")
    private List<String> toIds;

    /**
     * 检验类型
     */
    @NotNull(message = "checkType不能为空")
    private Integer checkType;
}
