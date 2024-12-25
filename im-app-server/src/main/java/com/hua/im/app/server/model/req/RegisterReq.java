package com.hua.im.app.server.model.req;

@Data
public class RegisterReq {

    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "请选择注册方式")
    //注册方式 1手机号注册 2用户名
    private Integer registerType;

    private String proto;

}
