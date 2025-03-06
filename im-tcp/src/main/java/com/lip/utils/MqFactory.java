package com.lip.utils;

import com.lip.config.BootStrapConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author: Elon
 * @title: MqFactory
 * @projectName: IM-System
 * @description: MQ工厂 初始化mq
 * @date: 2025/3/5 1:24
 */
public class MqFactory {

    // 连接工厂
    private static ConnectionFactory factory = null;

    // 管道
    private static Channel defaultChannel;

    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 建立连接
     *
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    private static Connection getConnection() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        return connection;
    }

    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        Channel channel = channelMap.get(channelName);
        if (channel == null) {
            channel = getConnection().createChannel();
            channelMap.put(channelName, channel);
        }
        return channel;
    }

    /**
     * 初始化方法
     * @param rabbitmq
     */
    public static void init(BootStrapConfig.Rabbitmq rabbitmq) {
        // 针对MQFactory初始化
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(rabbitmq.getHost());
            factory.setPort(rabbitmq.getPort());
            factory.setUsername(rabbitmq.getUserName());
            factory.setPassword(rabbitmq.getPassword());
            factory.setVirtualHost(rabbitmq.getVirtualHost());
        }
    }


}
