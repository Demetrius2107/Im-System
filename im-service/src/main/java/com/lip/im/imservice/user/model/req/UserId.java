package com.lip.im.imservice.user.model.req;

import com.hua.im.imcommon.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Shukun.Li
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserId extends RequestBase {

    private String userId;
}
