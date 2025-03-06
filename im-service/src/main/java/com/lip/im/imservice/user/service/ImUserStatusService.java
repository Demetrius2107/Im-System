package com.lip.im.imservice.user.service;

import com.lip.im.imservice.user.model.UserStatusChangeNotifyContent;
import com.lip.im.imservice.user.model.resp.UserOnlineStatusResp;

import java.util.Map;

/**
 * @author: Elon
 * @title: ImUserStatusService
 * @projectName: IM-System
 * @description: TODO
 * @date: 2025/3/6 17:21
 */
public interface ImUserStatusService {

    public void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content);

    void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req);

    void setUserCustomerStatus(SetUserCustomerStatusReq req);

    Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req);

    Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req);

}
