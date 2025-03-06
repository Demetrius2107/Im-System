package com.lip.im.imservice.user.service;


import com.lip.im.model.ResponseVO;
import com.lip.im.imservice.user.dao.ImUserDataEntity;
import com.lip.im.imservice.user.model.req.*;
import com.lip.im.imservice.user.model.resp.GetUserInfoResp;

public interface ImUserService {

    /**
     * 导入用户
     *
     * @param req 导入用户请求体
     * @return ResponseVO
     */
    public ResponseVO importUser(ImportUserReq req);

    /**
     * 获取用户信息
     *
     * @param req 获取用户信息请求体
     * @return ResponseVO
     */
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个用户信息
     *
     * @param userId 用户id
     * @param appId  appId
     * @return ResponseVO
     */
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId);

    /**
     * 删除用户
     *
     * @param req 删除用户信息请求体
     * @return ResponseVO
     */
    public ResponseVO deleteUser(DeleteUserReq req);

    /**
     * 修改用户信息
     *
     * @param req 修改用户信息请求体
     * @return ResponseVO
     */
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req);

    public ResponseVO login(LoginReq req);

    ResponseVO getUserSequence(GetUserSequenceReq req);
}
