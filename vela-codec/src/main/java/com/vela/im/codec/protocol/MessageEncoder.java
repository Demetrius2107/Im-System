package com.vela.im.codec.protocol;

import com.alibaba.fastjson.JSONObject;
import com.vela.im.codec.protocol.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>Title: MessageEncoder</p>
 * <p>Description: 消息编码器，Netty MessageToByteEncoder 实现，将 MessagePack 编码为二进制协议数据。</p>
 * <p>私有协议规则：前4位长度 + 4位command + JSON序列化body</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class MessageEncoder extends MessageToByteEncoder {

    /**
     * 编码消息包为二进制协议数据。
     * 协议格式: command(4B) + body长度(4B) + JSON序列化body
     *
     * @param ctx Netty通道处理器上下文
     * @param msg 待编码的消息对象，须为 MessagePack 类型
     * @param out 输出ByteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof MessagePack){
            MessagePack msgBody = (MessagePack) msg;
            String s = JSONObject.toJSONString(msgBody.getData());
            byte[] bytes = s.getBytes();
            out.writeInt(msgBody.getCommand());
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }

}
