package com.hua.im.imservice.user.model.resp;

import com.hua.im.imcommon.model.UserSession;

import java.util.List;

/**
 * @author Shukun.Li
 */
public class UserOnlineStatusResp {

    private List<UserSession> session;

    private String customText;

    private Integer customStatus;
}
