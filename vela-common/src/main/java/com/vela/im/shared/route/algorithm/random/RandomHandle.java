package com.vela.im.shared.route.algorithm.random;



import com.vela.im.shared.types.enums.UserErrorCode;
import com.vela.im.shared.exception.ApplicationException;
import com.vela.im.shared.route.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
