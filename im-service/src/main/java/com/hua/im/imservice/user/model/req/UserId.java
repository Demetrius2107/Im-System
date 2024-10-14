package com.hua.im.imservice.user.model.req;

import com.hua.im.imcommon.model.RequestBase;
import lombok.Data;

@Data
public class UserId extends RequestBase {

    private String userId;
}
