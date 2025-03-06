package com.lip.reciver;

import com.alibaba.fastjson.JSONObject;
import com.lip.im.model.constants.Constants;
import com.lip.im.model.enums.DeviceMultiLoginEnum;
import com.lip.im.model.enums.command.SystemCommand;
import com.lip.im.model.model.UserClientDto;
import com.lip.proto.MessagePack;
import com.lip.redis.RedisManager;
import com.lip.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: Elon
 * @title: UserLoginMessageListener
 * @projectName: IM-System
 * @description: 用户登录消息监听器  用户下线处理
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
                logger.info("收到用户上线通知：" + msg);
                UserClientDto dto = JSONObject.parseObject(msg, UserClientDto.class);
                List<NioSocketChannel> nioSocketChannels = SessionSocketHolder.get(dto.getAppId(), dto.getUserId());

                for (NioSocketChannel nioSocketChannel : nioSocketChannels) {
                    // 单端登录
                    if(loginModel == DeviceMultiLoginEnum.ONE.getLoginMode()){
                        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();

                        if(!(clientType + ":" + imei).equals(dto.getClientType()+":"+dto.getImei())){
                            MessagePack<Object> pack = new MessagePack<>();
                            pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                            nioSocketChannel.writeAndFlush(pack);
                        }

                    }
                    // 双端登录
                    else if(loginModel == DeviceMultiLoginEnum.TWO.getLoginMode()){
                        if(dto.getClientType() == com.lld.im.common.ClientType.WEB.getCode()){
                            continue;
                        }
                        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();

                        if (clientType == com.lld.im.common.ClientType.WEB.getCode()){
                            continue;
                        }
                        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();
                        if(!(clientType + ":" + imei).equals(dto.getClientType()+":"+dto.getImei())){
                            MessagePack<Object> pack = new MessagePack<>();
                            pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                            nioSocketChannel.writeAndFlush(pack);
                        }

                    }
                    // 多端登录
                    else if(loginModel == DeviceMultiLoginEnum.THREE.getLoginMode()){

                        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();
                        if(dto.getClientType() == com.lld.im.common.ClientType.WEB.getCode()){
                            continue;
                        }

                        Boolean isSameClient = false;
                        if((clientType == com.lld.im.common.ClientType.IOS.getCode() ||
                                clientType == com.lld.im.common.ClientType.ANDROID.getCode()) &&
                                (dto.getClientType() == com.lld.im.common.ClientType.IOS.getCode() ||
                                        dto.getClientType() == com.lld.im.common.ClientType.ANDROID.getCode())){
                            isSameClient = true;
                        }

                        if((clientType == com.lld.im.common.ClientType.MAC.getCode() ||
                                clientType == com.lld.im.common.ClientType.WINDOWS.getCode()) &&
                                (dto.getClientType() == com.lld.im.common.ClientType.MAC.getCode() ||
                                        dto.getClientType() == com.lld.im.common.ClientType.WINDOWS.getCode())){
                            isSameClient = true;
                        }

                        if(isSameClient && !(clientType + ":" + imei).equals(dto.getClientType()+":"+dto.getImei())){
                            MessagePack<Object> pack = new MessagePack<>();
                            pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                            nioSocketChannel.writeAndFlush(pack);
                        }
                    }
                }


            }
        });
    }


}
