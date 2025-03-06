package com.lip.handler;

import com.lip.im.model.constants.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Elon
 * @title: HeartBeatHandler
 * @projectName: IM-System
 * @description: 心跳处理器
 * @date: 2025/3/5 0:37
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {


    private Long heartBeatTime;

    public HeartBeatHandler(Long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }


    /**
     * 用户事件触发器
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            // 强制类型转换 IdleStateEvent
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
