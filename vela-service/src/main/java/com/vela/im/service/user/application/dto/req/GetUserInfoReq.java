package com.vela.im.service.user.application.dto.req;


import com.vela.im.shared.types.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 */
@Data
public class GetUserInfoReq extends RequestBase {

    /**
     * 用户ID集合
     */
    private List<String> userIds;
}
