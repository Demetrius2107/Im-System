package com.lip.im.service.user.application.dto.req;

import com.lip.im.shared.types.RequestBase;
import lombok.Data;

@Data
public class SetUserCustomerStatusReq extends RequestBase {

    private String userId;

    private String customText;

    private Integer customStatus;

}
