package com.hua.im.imservice.user.model.req;

import com.hua.im.imcommon.model.RequestBase;
import lombok.Data;

import java.util.List;

@Data
public class GetUserInfoReq extends RequestBase {

    private List<String> userIds;
}
