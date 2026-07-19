package com.lip.im.service.user.infrastructure.service;

import com.lip.im.service.user.application.dto.UserStatusChangeNotifyContent;
import com.lip.im.service.user.application.dto.req.PullFriendOnlineStatusReq;
import com.lip.im.service.user.application.dto.req.PullUserOnlineStatusReq;
import com.lip.im.service.user.application.dto.req.SetUserCustomerStatusReq;
import com.lip.im.service.user.application.dto.req.SubscribeUserOnlineStatusReq;
import com.lip.im.service.user.application.dto.resp.UserOnlineStatusResp;
import com.lip.im.service.user.domain.service.ImUserStatusService;
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
