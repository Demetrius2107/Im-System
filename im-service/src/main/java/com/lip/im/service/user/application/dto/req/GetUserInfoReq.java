package com.lip.im.service.user.application.dto.req;


import com.lip.im.shared.types.RequestBase;
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
