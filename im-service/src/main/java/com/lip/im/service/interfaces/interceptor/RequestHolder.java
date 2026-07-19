package com.lip.im.service.interfaces.interceptor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-06
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */public class RequestHolder {

    private final static ThreadLocal<Boolean> requestHolder = new ThreadLocal<>();

    public static void set(Boolean isadmin) {
        requestHolder.set(isadmin);
    }

    public static Boolean get() {
        return requestHolder.get();
    }

    public static void remove() {
        requestHolder.remove();
    }
}
