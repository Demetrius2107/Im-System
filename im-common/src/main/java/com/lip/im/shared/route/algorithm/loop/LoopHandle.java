package com.lip.im.shared.route.algorithm.loop;



import com.lip.im.shared.types.enums.UserErrorCode;
import com.lip.im.shared.exception.ApplicationException;
import com.lip.im.shared.route.RouteHandle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>Title: LoopHandle</p>
 * <p>Description: 轮询路由策略，按顺序循环选择服务器节点。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class LoopHandle implements RouteHandle {

    // 保证线程安全
    private AtomicLong index = new AtomicLong();

    @Override
    public String routeServer(List<String> values, String key) {
        int size = values.size();
        if(size == 0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }

        // 取模递增获取
        Long l = index.incrementAndGet() % size;

        // 到达最后，直接重新初始化
        if(l < 0){
            l = 0L;
        }
        return values.get(l.intValue());
    }
}
