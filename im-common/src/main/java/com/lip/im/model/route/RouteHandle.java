package com.lip.im.model.route;

import java.util.List;

/**
 * @description:
 * @author: lld
 * @version: 1.0
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
