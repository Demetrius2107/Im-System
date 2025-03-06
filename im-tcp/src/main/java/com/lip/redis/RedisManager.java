package com.lip.redis;

import com.lip.config.BootStrapConfig;
import com.lip.reciver.UserLoginMessageListener;
import org.redisson.api.RedissonClient;

/**
 * @author: Elon
 * @title: RedisManager
 * @projectName: IM-System
 * @description: Redis管理器 封装Redis 用户信息
 * @date: 2025/3/5 1:09
 */
public class RedisManager {

    // Redis客户端
    private static RedissonClient redissonClient;

    private static Integer loginModel;

    // 初始化
    public static void init(BootStrapConfig config){
        loginModel = config.getLim().getLoginModel();
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getLim().getRedis());
        UserLoginMessageListener userLoginMessageListener = new UserLoginMessageListener(loginModel);
        userLoginMessageListener.listenerUserLogin();
    }

    // 获取私有变量
    public static RedissonClient getRedissonClient(){
        return redissonClient;
    }




}
