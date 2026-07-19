package com.vela.im.shared.exception;

/**
 * <p>Title: ApplicationExceptionEnum</p>
 * <p>Description: 应用异常枚举接口，所有错误码枚举需实现此接口，统一错误码定义规范。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public interface ApplicationExceptionEnum {

    /** 获取错误码 */
    int getCode();

    /** 获取错误消息 */
    String getError();
}
