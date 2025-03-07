package com.lip.im.model.route.algorithm.consistenthash;


import com.lip.im.model.route.RouteHandle;

import java.util.List;

/**
 * @description: 一致性哈希算法路由策略
 * @author: lld
 * @version: 1.0
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
