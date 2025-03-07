package com.lip.im.imservice.utils;

import com.alibaba.fastjson.JSONObject;
import com.lip.im.model.constants.Constants;
import com.lip.im.model.enums.ImConnectStatusEnum;
import com.lip.im.model.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Component
public class UserSessionUtils {

    public Object get;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //1.获取用户所有的session

    public List<UserSession> getUserSession(Integer appId, String userId){

        // key appId + UserId 从Redis中获取
        String userSessionKey = appId + Constants.RedisConstants.UserSessionConstants
                + userId;

        Map<Object, Object> entries =
                stringRedisTemplate.opsForHash().entries(userSessionKey);
        // UserSession 返回值集合
        List<UserSession> list = new ArrayList<>();
        // 获取所有values
        Collection<Object> values = entries.values();

        for (Object o : values){
            // 转换为String类型
            String str = (String) o;
            UserSession session =
                    JSONObject.parseObject(str, UserSession.class);
            if(session.getConnectState() == ImConnectStatusEnum.ONLINE_STATUS.getCode()){
                list.add(session);
            }
        }
        return list;
    }

    //2.获取用户除了本端的session 指定端Session
    public UserSession getUserSession(Integer appId,String userId
            ,Integer clientType,String imei){

        String userSessionKey = appId + Constants.RedisConstants.UserSessionConstants
                + userId;
        String hashKey = clientType + ":" + imei;
        Object o = stringRedisTemplate.opsForHash().get(userSessionKey, hashKey);
        UserSession session =
                JSONObject.parseObject(o.toString(), UserSession.class);
        return session;
    }


}
