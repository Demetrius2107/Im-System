package com.lip.im.tcp;

import com.lip.im.codec.config.BootStrapConfig;
import com.lip.im.tcp.interfaces.reciver.MessageReceiver;
import com.lip.im.tcp.infrastructure.redis.RedisManager;
import com.lip.im.tcp.infrastructure.register.RegistryZK;
import com.lip.im.tcp.infrastructure.register.Zkit;
import com.lip.im.tcp.interfaces.server.LimServer;
import com.lip.im.tcp.interfaces.server.LimWebSocketServer;
import com.lip.im.tcp.infrastructure.utils.MqFactory;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wanqiu
 * @title: Starter
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/3 23:46
 */
public class Starter {

    // HTTP GET POST PUT DELETE 1.0 1.1 2.0
    // client IOS 安卓 pc(windows mac) web //支持json 也支持 protobuf
    // appId
    // 28 + imei + body
    // 请求头（指令 版本 clientType 消息解析类型 imei长度 appId bodylen）+ imei号 + 请求体
    // len+body

    public static void main(String[] args) {
        if(args.length > 0){
            start(args[0]);
        }
    }


    private static void start(String path){
        try{
            // 读取config配置
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(path);
            BootStrapConfig bootStrapConfig = yaml.loadAs(inputStream,BootStrapConfig.class);

            // 启动tcp网关
            new LimServer(bootStrapConfig.getLim()).start();
            new LimWebSocketServer(bootStrapConfig.getLim()).start();

            // 初始化redis
            RedisManager.init(bootStrapConfig);
            // 初始化mq
            MqFactory.init(bootStrapConfig.getLim().getRabbitmq());
            MessageReceiver.init(bootStrapConfig.getLim().getBrokerId() + "");
            // zookeeper注册
            registerZK(bootStrapConfig);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(500);
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(500);
            throw new RuntimeException(e);
        }
    }

    public static void registerZK(BootStrapConfig config) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        ZkClient zkClient = new ZkClient(config.getLim().getZkConfig().getZkAddr(),
                config.getLim().getZkConfig().getZkConnectTimeOut());
        Zkit zkit = new Zkit(zkClient);
        RegistryZK registryZK = new RegistryZK(zkit, hostAddress, config.getLim());
        Thread thread = new Thread(registryZK);
        thread.start();

    }

}
