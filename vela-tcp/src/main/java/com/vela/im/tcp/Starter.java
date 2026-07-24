package com.vela.im.tcp;

import com.vela.im.codec.config.BootStrapConfig;
import com.vela.im.tcp.infrastructure.redis.RedisManager;
import com.vela.im.tcp.infrastructure.register.RegistryZK;
import com.vela.im.tcp.infrastructure.register.Zkit;
import com.vela.im.tcp.infrastructure.utils.MqFactory;
import com.vela.im.tcp.interfaces.reciver.MessageReceiver;
import com.vela.im.tcp.interfaces.server.LimServer;
import com.vela.im.tcp.interfaces.server.LimWebSocketServer;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>Title: Starter</p>
 * <p>Description: 服务启动入口，加载配置、启动 TCP/WebSocket 网关、初始化 Redis/MQ/ZK 组件</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public class Starter {

    private static final Logger log = LoggerFactory.getLogger(Starter.class);

    // HTTP GET POST PUT DELETE 1.0 1.1 2.0
    // client IOS 安卓 pc(windows mac) web //支持json 也支持 protobuf
    // appId
    // 28 + imei + body
    // 请求头（指令 版本 clientType 消息解析类型 imei长度 appId bodylen）+ imei号 + 请求体
    // len+body

    public static void main(String[] args) {
        if (args.length > 0) {
            start(args[0]);
        } else {
            log.error("Usage: java Starter <config-path>");
            System.exit(1);
        }
    }

    private static void start(String path) {
        log.info("Loading configuration from: {}", path);

        // 读取config配置
        BootStrapConfig bootStrapConfig;
        try (InputStream inputStream = new FileInputStream(path)) {
            Yaml yaml = new Yaml();
            bootStrapConfig = yaml.loadAs(inputStream, BootStrapConfig.class);
        } catch (Exception e) {
            log.error("Failed to load config file: {}", path, e);
            System.exit(500);
            return;
        }

        // 校验配置
        BootStrapConfig.ServerConfig tcpConfig = bootStrapConfig.getServerConfig();
        if (tcpConfig == null) {
            log.error("Config missing: 'serverConfig' section is required");
            System.exit(500);
            return;
        }

        // 启动tcp网关
        log.info("Starting TCP server on port: {}", tcpConfig.getTcpPort());
        new LimServer(tcpConfig).start();
        new LimWebSocketServer(tcpConfig).start();

        // 初始化redis
        log.info("Initializing Redis connection");
        RedisManager.init(bootStrapConfig);

        // 初始化mq
        if (tcpConfig.getRabbitmqConfig() != null) {
            log.info("Initializing RabbitMQ connection");
            MqFactory.init(tcpConfig.getRabbitmqConfig());
        }

        log.info("Initializing message receiver for brokerId: {}", tcpConfig.getBrokerId());
        MessageReceiver.init(tcpConfig.getBrokerId() + "");

        // zookeeper注册
        log.info("Registering to ZooKeeper");
        registerZK(bootStrapConfig);

        log.info("Starter completed successfully");
    }

    private static void registerZK(BootStrapConfig config) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            ZkClient zkClient = new ZkClient(config.getServerConfig().getZookeeperConfig().getZkAddr(),
                    config.getServerConfig().getZookeeperConfig().getZkConnectTimeOut());
            Zkit zkit = new Zkit(zkClient);
            RegistryZK registryZK = new RegistryZK(zkit, hostAddress, config.getServerConfig());
            Thread thread = new Thread(registryZK, "zk-registry");
            thread.start();
            log.info("ZooKeeper registration started, addr: {}", hostAddress);
        } catch (UnknownHostException e) {
            log.error("Failed to get local host address for ZK registration", e);
            System.exit(500);
        }
    }

}