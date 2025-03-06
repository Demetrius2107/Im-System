package com.lip.im.imservice.user.model.resp;

import com.lip.im.model.model.UserSession;

import java.util.List;

/**
 * @author Shukun.Li
 */
public class UserOnlineStatusResp {

    private List<UserSession> session;

    private String customText;

    private Integer customStatus;
}
