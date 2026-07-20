package com.vela.im.service.user.domain.service;


import com.vela.im.shared.base.Result;
import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.service.user.application.dto.req.*;
import com.vela.im.service.user.application.dto.resp.GetUserInfoResp;

public interface ImUserService {

    /**
     * 导入用户
     *
     * @param req 导入用户请求体
     * @return Result
     */
    public Result importUser(ImportUserReq req);

    /**
     * 获取用户信息
     *
     * @param req 获取用户信息请求体
     * @return Result
     */
    public Result<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个用户信息
     *
     * @param userId 用户id
     * @param appId  appId
     * @return Result
     */
    public Result<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId);

    /**
     * 删除用户
     *
     * @param req 删除用户信息请求体
     * @return Result
     */
    public Result deleteUser(DeleteUserReq req);

    /**
     * 修改用户信息
     *
     * @param req 修改用户信息请求体
     * @return Result
     */
    public Result modifyUserInfo(ModifyUserInfoReq req);

    public Result login(LoginReq req);

    Result getUserSequence(GetUserSequenceReq req);
}
