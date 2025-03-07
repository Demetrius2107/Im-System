package com.lip.im.model.route.algorithm.loop;



import com.lip.im.model.enums.UserErrorCode;
import com.lip.im.model.exception.ApplicationException;
import com.lip.im.model.route.RouteHandle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @description: 轮询路由策略
 * @author: lld
 * @version: 1.0
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
