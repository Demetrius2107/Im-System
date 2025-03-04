package com.lip.redis;

import com.lip.config.BootStrapConfig;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

/**
 * @author: Elon
 * @title: SingleClientStrategy
 * @projectName: IM-System
 * @description: 单一客户端登录策略
 * @date: 2025/3/5 1:13
 */
public class SingleClientStrategy {

    public RedissonClient getRedissonClient(BootStrapConfig.RedisConfig redisConfig){
        Config config = new Config();
        String node = redisConfig.getSingle().getAddress();
        node = node.startsWith("redis:://") ? node: "redis://" + node;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setDatabase(redisConfig.getDatabase())
                .setTimeout(redisConfig.getTimeout())
                .setConnectionMinimumIdleSize(redisConfig.getPoolMinIdle())
                .setConnectTimeout(redisConfig.getPoolConnTimeout())
                .setConnectionPoolSize(redisConfig.getPoolSize());

        if(StringUtils.isNotBlank(redisConfig.getPassword())){
            serverConfig.setPassword(redisConfig.getPassword());
        }
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

}
