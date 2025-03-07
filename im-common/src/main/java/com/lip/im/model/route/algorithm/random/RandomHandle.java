package com.lip.im.model.route.algorithm.random;



import com.lip.im.model.enums.UserErrorCode;
import com.lip.im.model.exception.ApplicationException;
import com.lip.im.model.route.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
public class RandomHandle implements RouteHandle {
    @Override
    public String routeServer(List<String> values, String key) {
        // 获取地址集合大小
        int size = values.size();
        if(size == 0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        // 随机获取
        int i = ThreadLocalRandom.current().nextInt(size);
        return values.get(i);
    }
}
