package com.vela.im.codec.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>Title: MessageEncoder</p>
 * <p>Description: 消息编码器，Netty MessageToByteEncoder 实现，将 MessagePack 编码为二进制协议数据。</p>
 * <p>协议格式: command(4B) + body长度(4B) + JSON序列化body</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-24
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class MessageEncoder extends MessageToByteEncoder<MessagePack> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, ByteBuf out) {
        String json = JSONObject.toJSONString(msg.getData());
        byte[] bytes = json.getBytes();
        out.writeInt(msg.getCommand());
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

}
