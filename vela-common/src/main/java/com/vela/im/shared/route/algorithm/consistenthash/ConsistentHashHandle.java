package com.vela.im.shared.route.algorithm.consistenthash;


import com.vela.im.shared.route.RouteHandle;

import java.util.List;

/**
 * <p>Title: ConsistentHashHandle</p>
 * <p>Description: 一致性哈希算法路由策略，基于 TreeMap 实现哈希环。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class ConsistentHashHandle implements RouteHandle {

    //TreeMap
    private AbstractConsistentHash hash;

    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    @Override
    public String routeServer(List<String> values, String key) {
        return hash.process(values,key);
    }
}
