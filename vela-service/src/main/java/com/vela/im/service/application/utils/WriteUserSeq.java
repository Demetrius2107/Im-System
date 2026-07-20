package com.vela.im.service.application.utils;


import com.vela.im.shared.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>Title: WriteUserSeq</p>
 * <p>Description: 用户序列号写入服务，将用户各类型（好友/群组/会话）的序列号写入 Redis Hash。</p>
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
public class WriteUserSeq {

    private final RedisTemplate<String, Object> redisTemplate;

    public WriteUserSeq(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void writeUserSeq(Integer appId,String userId,String type,Long seq){
        String key = appId + ":" + Constants.RedisConstants.SeqPrefix + ":" + userId;
        redisTemplate.opsForHash().put(key,type,seq);
    }

}
