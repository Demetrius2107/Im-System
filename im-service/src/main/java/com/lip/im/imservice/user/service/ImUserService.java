package com.lip.im.imservice.user.service;

import com.hua.im.imcommon.ResponseVO;
import com.lip.im.imservice.user.dao.ImUserDataEntity;
import com.lip.im.imservice.user.model.req.DeleteUserReq;
import com.lip.im.imservice.user.model.req.GetUserInfoReq;
import com.lip.im.imservice.user.model.req.ImportUserReq;
import com.lip.im.imservice.user.model.req.ModifyUserInfoReq;
import com.lip.im.imservice.user.model.resp.GetUserInfoResp;
import com.lip.im.imservice.user.model.resp.ImportUserResp;

public interface ImUserService {

    /**
     * 导入用户
     *
     * @param req 导入用户请求体
     * @return ResponseVO
     */
    public ResponseVO<ImportUserResp> importUser(ImportUserReq req);

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
    public ResponseVO<ImportUserResp> deleteUser(DeleteUserReq req);

    /**
     * 修改用户信息
     *
     * @param req 修改用户信息请求体
     * @return ResponseVO
     */
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req);
}
