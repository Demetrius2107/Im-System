package com.vela.im.shared.exception;

import com.vela.im.shared.exception.ApplicationExceptionEnum;

/**
 * <p>Title: BaseErrorCode</p>
 * <p>Description: 基础错误码枚举，实现 ApplicationExceptionEnum 接口，定义通用成功/系统错误码。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public enum BaseErrorCode implements ApplicationExceptionEnum {

    /** 成功 */
    SUCCESS(200,"success"),
    /** 服务器内部错误，请联系管理员 */
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
