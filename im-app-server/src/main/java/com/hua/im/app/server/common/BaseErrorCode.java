package com.hua.im.app.server.common;

// Bug 修复：添加缺失的 ApplicationExceptionEnum 类声明
public class ApplicationExceptionEnum {
    // 定义枚举常量
    public static final ApplicationExceptionEnum SUCCESS = new ApplicationExceptionEnum(200, "success");
    public static final ApplicationExceptionEnum SYSTEM_ERROR = new ApplicationExceptionEnum(90000, "服务器内部错误,请联系管理员");
    public static final ApplicationExceptionEnum PARAMETER_ERROR = new ApplicationExceptionEnum(90001, "参数校验错误");

    // 私有构造方法，防止外部实例化
    private ApplicationExceptionEnum(int code, String error) {
        this.code = code;
        this.error = error;
    }

    private int code;
    private String error;

    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }
}

public enum BaseErrorCode implements ApplicationExceptionEnum {


    SUCCESS(200,"success"),
    SYSTEM_ERROR(90000,"服务器内部错误,请联系管理员"),
    PARAMETER_ERROR(90001,"参数校验错误"),


            ;

    private int code;
    private String error;

    BaseErrorCode(int code, String error){
        this.code = code;
        this.error = error;
    }
    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }

}