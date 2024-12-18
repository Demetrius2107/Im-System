package com.hua.im.imservice.user.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: ShuKun.Li
 * @description: 数据库用户数据实体类
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user_data")
public class ImUserDataEntity {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 位置
     */
    private String location;

    /**
     * 生日
     */
    private String birthDay;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String photo;

    /**
     * 用户性别
     */
    private Integer userSex;

    /**
     * 个性签名
     */
    private String selfSignature;

    /**
     * 添加好友验证类型
     * Friend_AllowType
     * 1 需要验证
     * 0 无需验证
     */
    private Integer friendAllowType;

    /**
     * 管理员禁止用户添加好友
     * 0 未禁用
     * 1 已禁用
     */
    private Integer disableAddFriend;

    /**
     * 禁用标识
     * 0 未禁用 1 已禁用
     */
    private Integer forbiddenFlag;

    /**
     * 禁言标识
     */
    private Integer silentFlag;

    /**
     * 用户类型 1普通用户 2客服 3 机器人
     */
    private Integer userType;

    /**
     * appId
     */
    private Integer appId;

    /**
     * 删除标记
     */
    private Integer delFlag;

    /**
     * 拓展
     */
    private String extra;

}
