package com.vela.im.codec.protocol;


import com.vela.im.codec.protocol.Message;
import com.vela.im.codec.utils.ByteBufToMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * <p>Title: WebSocketMessageDecoder</p>
 * <p>Description: WebSocket 消息解码器，将 BinaryWebSocketFrame 解码为 Message 协议对象。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    /**
     * 解码 WebSocket 二进制帧为 Message 协议对象。
     *
     * @param ctx Netty通道处理器上下文
     * @param msg WebSocket二进制帧
     * @param out 解码后的消息对象列表
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {

        ByteBuf content = msg.content();
        if (content.readableBytes() < 28) {
            return;
        }
        Message message = ByteBufToMessageUtils.transition(content);
        if(message == null){
            return;
        }
        out.add(message);
    }
}
