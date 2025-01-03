package com.hua.im.imcommon.enums;

import com.hua.im.imcommon.exception.ApplicationExceptionEnum;

/**
 * @author Shukun.Li
 */

public enum UserErrorCode implements ApplicationExceptionEnum {

    IMPORT_SIZE_BEYOND(20000, "导入数量超出上限"),
    USER_IS_NOT_EXIST(20001, "用户不存在"),
    SERVER_GET_USER_ERROR(20002, "用户获取服务失败"),
    MODIFY_USER_ERROR(20003, "更新用户失败"),
    SERVER_NOT_AVAILABLE(71000, "没有可用的服务"),
    ;

    private int code;

    private String error;

    UserErrorCode(int code, String error) {
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
