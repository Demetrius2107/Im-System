package com.vela.im.tcp.interfaces.handler;

import com.vela.im.shared.constants.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Title: HeartBeatHandler</p>
 * <p>Description: Netty 心跳检测处理器，监听读/写/全空闲事件，超时后执行退后台逻辑</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-05
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    /** 心跳检测超时时间（毫秒） */
    private final Long heartBeatTime;

    /**
     * 构造心跳处理器
     *
     * @param heartBeatTime 心跳超时时间（毫秒）
     */
    public HeartBeatHandler(Long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    /**
     * 用户事件触发器，处理 IdleStateEvent 空闲状态事件
     *
     * @param ctx 通道处理器上下文
     * @param evt 事件对象
     * @throws Exception 处理异常
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("读空闲");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("进入写空闲");
            } else if (event.state() == IdleState.ALL_IDLE) {
                Long lastReadTime = (Long) ctx.channel()
                        .attr(AttributeKey.valueOf(Constants.ReadTime)).get();

                long now = System.currentTimeMillis();

                if(lastReadTime != null && now -lastReadTime > heartBeatTime){
                    // TODO 退后台逻辑
                }
            }
        }
    }
}