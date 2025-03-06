package com.lip.im.imservice.user.model.req;

import com.lip.im.model.model.RequestBase;
import lombok.Data;

@Data
public class SetUserCustomerStatusReq extends RequestBase {

    private String userId;

    private String customText;

    private Integer customStatus;

}
