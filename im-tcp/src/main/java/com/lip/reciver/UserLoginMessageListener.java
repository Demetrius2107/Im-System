package com.lip.reciver;

import com.alibaba.fastjson.JSONObject;
import com.lip.constants.Constants;
import com.lip.model.UserClientDto;
import com.lip.redis.RedisManager;
import com.lip.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: Elon
 * @title: UserLoginMessageListener
 * @projectName: IM-System
 * @description: 用户登录消息监听器
 * 多端同步：1单端登录：一端在线：踢掉除了本clientType + imel 的设备
 *         2双端登录：允许pc/mobile 其中一端登录 + web端 踢掉除了本clientType + imel 以外的web端设备
 *         3 三端登录：允许手机+pc+web，踢掉同端的其他imei 除了web
 *         4 不做任何处理
 * @date: 2025/3/5 1:21
 */
public class UserLoginMessageListener {

    private final static Logger logger = LoggerFactory.getLogger(UserLoginMessageListener.class);

    private Integer loginModel;

    public UserLoginMessageListener(Integer loginModel){
        this.loginModel = loginModel;
    }

    public void listenerUserLogin(){
        RTopic topic = RedisManager.getRedissonClient().getTopic(Constants.RedisConstants.UserLoginChannel);
        topic.addListener(String.class, new MessageListener<String>() {

            @Override
            public void onMessage(CharSequence charSequence, String msg) {
                logger.info("收到用户上线通知: " + msg);
                UserClientDto dto = JSONObject.parseObject(msg,UserClientDto.class);

                List<NioSocketChannel> nioSocketChannels = SessionSocketHolder.get(dto.getAppId(), dto.getUserId());

                // NioSocketChannel 循环 判断存储的每一个Session
                for(NioSocketChannel nioSocketChannel : nioSocketChannels){

                }

            }
        });
    }


}
