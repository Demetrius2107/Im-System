package com.hua.im.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shukun.Li
 */
public class LimServer {

    // 日志类
    private final static Logger logger = LoggerFactory.getLogger(LimServer.class);

    // 端口号
    @Getter
    private int port;


    EventLoopGroup mainGroup;
    EventLoopGroup subGroup;
    ServerBootstrap server;


    public LimServer(Integer port){
        this.port = port;
        mainGroup = new NioEventLoopGroup();
    }


}
