package com.vela.im.codec.protocol;


import com.alibaba.fastjson.JSONObject;
import com.vela.im.codec.protocol.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>Title: WebSocketMessageEncoder</p>
 * <p>Description: WebSocket 消息编码器，将 MessagePack 编码为 BinaryWebSocketFrame。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack> {

    /** 日志记录器 */
    private static Logger log = LoggerFactory.getLogger(WebSocketMessageEncoder.class);

    /**
     * 将 MessagePack 编码为 WebSocket BinaryWebSocketFrame。
     * 协议格式: command(4B) + body长度(4B) + JSON序列化body
     *
     * @param ctx Netty通道处理器上下文
     * @param msg 待编码的消息包
     * @param out 编码后的消息帧列表
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, List<Object> out)  {

        try {
            String s = JSONObject.toJSONString(msg);
            ByteBuf byteBuf = Unpooled.directBuffer(8+s.length());
            byte[] bytes = s.getBytes();
            byteBuf.writeInt(msg.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(new BinaryWebSocketFrame(byteBuf));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}