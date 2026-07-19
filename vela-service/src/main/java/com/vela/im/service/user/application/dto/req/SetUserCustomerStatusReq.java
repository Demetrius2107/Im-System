package com.vela.im.service.user.application.dto.req;

import com.vela.im.shared.types.RequestBase;
import lombok.Data;

@Data
public class SetUserCustomerStatusReq extends RequestBase {

    private String userId;

    private String customText;

    private Integer customStatus;

}
