package com.hua.im.app.server.model.resp;

import lombok.Data;

/**
 * @author Shukun.Li
 */
@Data
public class LoginResp {

    //im的token
    private String imUserSign;

    //自己的token
    private String userSign;

    private String userId;

    private Integer appId;

}
