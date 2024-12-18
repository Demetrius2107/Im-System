package com.hua.im.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimServer {

    // 日志类
    private final static Logger logger = LoggerFactory.getLogger(LimServer.class);

    // 端口号
    private int port;

    EventLoopGroup mainGroup;
    EventLoopGroup subGroup;
    ServerBootstrap server;


    public LimServer(Integer port){
        this.port = port;
        mainGroup = new NioEventLoopGroup();
    }


}
