package com.lip.im.imservice.user.service.impl;

import com.lip.im.imservice.user.model.UserStatusChangeNotifyContent;
import com.lip.im.imservice.user.model.req.PullFriendOnlineStatusReq;
import com.lip.im.imservice.user.model.req.PullUserOnlineStatusReq;
import com.lip.im.imservice.user.model.req.SetUserCustomerStatusReq;
import com.lip.im.imservice.user.model.req.SubscribeUserOnlineStatusReq;
import com.lip.im.imservice.user.model.resp.UserOnlineStatusResp;
import com.lip.im.imservice.user.service.ImUserStatusService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: Elon
 * @title: ImUserStatusServiceImpl
 * @projectName: IM-System
 * @description: TODO
 * @date: 2025/3/6 17:30
 */
@Service
public class ImUserStatusServiceImpl implements ImUserStatusService {


    @Override
    public void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content) {

    }

    @Override
    public void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req) {

    }

    @Override
    public void setUserCustomerStatus(SetUserCustomerStatusReq req) {

    }

    @Override
    public Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req) {
        return null;
    }

    @Override
    public Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req) {
        return null;
    }
}
