package com.lip.im.imservice.user.model.req;

import com.hua.im.imcommon.model.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author Shukun.Li
 */
@Data
public class GetUserInfoReq extends RequestBase {

    /**
     * 用户ID集合
     */
    private List<String> userIds;
}
