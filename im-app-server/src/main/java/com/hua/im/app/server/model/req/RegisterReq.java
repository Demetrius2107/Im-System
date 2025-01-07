package com.hua.im.app.server.model.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RegisterReq {

    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

      //注册方式 1手机号注册 2用户名
    @NotNull(message = "请选择注册方式")
    private Integer registerType;

    private String proto;

}
