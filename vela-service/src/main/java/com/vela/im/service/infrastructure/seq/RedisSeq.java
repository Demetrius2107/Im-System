package com.vela.im.service.infrastructure.seq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>Title: RedisSeq</p>
 * <p>Description: 基于 Redis 的分布式序列号生成器，使用 INCR 命令实现单调递增序列。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-06
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Service
public class RedisSeq {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisSeq(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取指定 key 的下一个序列号（原子递增）
     *
     * @param key 序列 key
     * @return 递增后的序列值
     */
    public long doGetSeq(String key){
        return stringRedisTemplate.opsForValue().increment(key);
    }
}
