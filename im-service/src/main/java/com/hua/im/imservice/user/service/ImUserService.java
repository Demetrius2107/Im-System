package com.hua.im.imservice.user.service;

import com.hua.im.imcommon.ResponseVO;
import com.hua.im.imservice.user.dao.ImUserDataEntity;
import com.hua.im.imservice.user.model.req.DeleteUserReq;
import com.hua.im.imservice.user.model.req.GetUserInfoReq;
import com.hua.im.imservice.user.model.req.ImportUserReq;
import com.hua.im.imservice.user.model.req.ModifyUserInfoReq;
import com.hua.im.imservice.user.model.resp.GetUserInfoResp;

public interface ImUserService {

    public ResponseVO importUser(ImportUserReq req);

    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId);

    public ResponseVO deleteUser(DeleteUserReq req);

    public ResponseVO modifyUserInfo(ModifyUserInfoReq req);
}
