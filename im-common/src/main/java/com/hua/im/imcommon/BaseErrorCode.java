package com.hua.im.imcommon;


import com.hua.im.imcommon.exception.ApplicationExceptionEnum;

/**
 * @author Shukun.Li
 */
public enum BaseErrorCode implements ApplicationExceptionEnum {

    SUCCESS(200,"success"),
    SYSTEM_ERROR(90000,"服务器内部错误,请联系管理员"),
    PARAMETER_ERROR(90001,"参数校验错误");

    /**
     * 状态码
     */
    private int code;

    /**
     * 错误信息
     */
    private String error;

    BaseErrorCode(int code, String error){
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getError() {
        return this.error;
    }

}
