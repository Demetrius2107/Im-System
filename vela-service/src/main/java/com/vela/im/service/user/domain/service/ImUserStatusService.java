package com.vela.im.service.user.domain.service;

import com.vela.im.service.user.application.dto.UserStatusChangeNotifyContent;
import com.vela.im.service.user.application.dto.req.PullFriendOnlineStatusReq;
import com.vela.im.service.user.application.dto.req.PullUserOnlineStatusReq;
import com.vela.im.service.user.application.dto.req.SetUserCustomerStatusReq;
import com.vela.im.service.user.application.dto.req.SubscribeUserOnlineStatusReq;
import com.vela.im.service.user.application.dto.resp.UserOnlineStatusResp;

import java.util.Map;

/**
 * @author wanqiu
 * @title: ImUserStatusService
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/6 17:21
 */
public interface ImUserStatusService {

    public void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content);

    void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req);

    void setUserCustomerStatus(SetUserCustomerStatusReq req);

    Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req);

    Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req);

}
