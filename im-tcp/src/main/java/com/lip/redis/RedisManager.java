package com.lip.redis;

import com.lip.config.BootStrapConfig;
import org.redisson.api.RedissonClient;

/**
 * @author: Elon
 * @title: RedisManager
 * @projectName: IM-System
 * @description: Redis管理器 封装Redis 用户信息
 * @date: 2025/3/5 1:09
 */
public class RedisManager {

    private static RedissonClient redissonClient;

    private static Integer loginModel;

    public static void init(BootStrapConfig config){
        loginModel = config.getLim().getLoginModel();
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getLim().getRedis());

    }

    public static RedissonClient getRedissonClient(){
        return redissonClient;
    }




}
