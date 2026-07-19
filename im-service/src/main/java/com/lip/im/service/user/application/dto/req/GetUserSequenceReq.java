package com.lip.im.service.user.application.dto.req;


import com.lip.im.shared.types.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author wanqiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;
}
