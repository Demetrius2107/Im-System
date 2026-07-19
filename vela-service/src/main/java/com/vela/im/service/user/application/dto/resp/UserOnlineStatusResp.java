package com.vela.im.service.user.application.dto.resp;

import com.vela.im.shared.types.UserSession;

import java.util.List;

/**
 * @author wanqiu
 */
public class UserOnlineStatusResp {

    private List<UserSession> session;

    private String customText;

    private Integer customStatus;
}
