package com.vela.im.codec.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: BootStrapConfig</p>
 * <p>Description: 服务端启动参数配置类，包含TCP/WebSocket端口、心跳超时、BrokerId、Redis/RabbitMQ/ZK连接配置。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-24
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class BootStrapConfig {

    /**
     * TCP/WebSocket 及中间件连接配置
     */
    private TcpConfig serverConfig;


    /**
     * <p>Title: TcpConfig</p>
     * <p>Description: TCP/WebSocket 服务器及中间件连接配置。</p>
     */
    @Data
    public static class TcpConfig {

        /**
         * TCP 绑定的端口号
         */
        private Integer tcpPort;

        /**
         * WebSocket 绑定的端口号
         */
        private Integer webSocketPort;

        /**
         * 是否启用 WebSocket
         */
        private boolean enableWebSocket;

        /**
         * Boss 线程数，默认 1
         */
        private Integer bossThreadSize;

        /**
         * Work 线程数
         */
        private Integer workThreadSize;

        /**
         * 心跳超时时间，单位毫秒
         */
        private Long heartBeatTime;

        /**
         * 登录模式（多端同步策略）：1-单端登录 2-双端登录 3-三端登录 4-多端登录
         */
        private Integer loginModel;

        /**
         * Redis 连接配置
         */
        private RedisConfig redis;

        /**
         * RabbitMQ 连接配置
         */
        private Rabbitmq rabbitmq;

        /**
         * ZooKeeper 连接配置
         */
        private ZkConfig zkConfig;

        /**
         * 当前 Broker 节点 ID
         */
        private Integer brokerId;

        /**
         * 业务逻辑层服务 URL（Feign 调用地址）
         */
        private String logicUrl;

    }

    @Data
    public static class ZkConfig {
        /**
         * zk连接地址
         */
        private String zkAddr;

        /**
         * zk连接超时时间
         */
        private Integer zkConnectTimeOut;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisConfig {

        /**
         * 单机模式：single 哨兵模式：sentinel 集群模式：cluster
         */
        private String mode;
        /**
         * 数据库
         */
        private Integer database;
        /**
         * 密码
         */
        private String password;
        /**
         * 超时时间
         */
        private Integer timeout;
        /**
         * 最小空闲数
         */
        private Integer poolMinIdle;
        /**
         * 连接超时时间(毫秒)
         */
        private Integer poolConnTimeout;
        /**
         * 连接池大小
         */
        private Integer poolSize;

        /**
         * redis单机配置
         */
        private RedisSingle single;

    }

    /**
     * redis单机配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSingle {
        /**
         * 地址
         */
        private String address;
    }

    /**
     * <p>Title: Rabbitmq</p>
     * <p>Description: RabbitMQ 连接配置。</p>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rabbitmq {

        /**
         * RabbitMQ 服务主机地址
         */
        private String host;

        /**
         * RabbitMQ 服务端口号
         */
        private Integer port;

        /**
         * 虚拟主机路径
         */
        private String virtualHost;

        /**
         * 登录用户名
         */
        private String userName;

        /**
         * 登录密码
         */
        private String password;
    }

}
