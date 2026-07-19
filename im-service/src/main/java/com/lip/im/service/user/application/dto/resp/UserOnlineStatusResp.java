package com.lip.im.service.user.application.dto.resp;

import com.lip.im.shared.types.UserSession;

import java.util.List;

/**
 * @author Shukun.Li
 */
public class UserOnlineStatusResp {

    private List<UserSession> session;

    private String customText;

    private Integer customStatus;
}
