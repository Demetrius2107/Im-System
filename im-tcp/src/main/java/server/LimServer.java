package server;

import config.BootStrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

/**
 * @author: Elon
 * @title: LimServer
 * @projectName: im-system
 * @description: TODO
 * @date: 2025/1/24 21:27
 */
public class LimServer {


    private final static Logger logger = (Logger) LoggerFactory.getLogger(LimServer.class);
    BootStrapConfig.TcpConfig config;

    EventLoopGroup mainGroup;

    EventLoopGroup subGroup;

    ServerBootstrap server;

    public LimServer (BootStrapConfig.TcpConfig config){
        this.config = config;
        mainGroup = new NioEventLoopGroup(config.getBoosThreadSize());
        subGroup = new NioEventLoopGroup(config.getWorkThreadSize());
        server = new ServerBootstrap();
        server.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,10240)
                .option(ChannelOption.SO_REUSEADDR,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new MessageDecoder());
                        socketChannel.pipeline().addLast(new MessageEncoder());
                    }
                });


    }




}
