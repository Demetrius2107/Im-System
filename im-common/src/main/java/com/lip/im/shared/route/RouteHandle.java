package com.lip.im.shared.route;

import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public interface RouteHandle {

    /**
     * 路由策略获取服务器地址
     * @param values
     * @param key
     * @return
     */
    public String routeServer(List<String> values,String key);

}
