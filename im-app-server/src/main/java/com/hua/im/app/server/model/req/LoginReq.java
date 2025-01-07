package com.hua.im.app.server.model.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class LoginReq {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

    // 登录方式 1用户名+密码 2手机号+验证码
    @NotNull(message = "请选择登录方式")
    private Integer loginType;

}
