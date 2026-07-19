package com.lip.im.codec.protocol;

import com.alibaba.fastjson.JSONObject;

import com.lip.im.codec.protocol.Message;
import com.lip.im.codec.utils.ByteBufToMessageUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>Title: MessageDecoder</p>
 * <p>Description: 消息解码器，Netty ByteToMessageDecoder 实现，将二进制流解码为 Message 协议对象。</p>
 * <p>私有协议规则：前4字节长度 + 4字节command + 变长body</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception {
        /** 请求头格式：指令(4B) + 版本(4B) + clientType(4B) + 消息解析类型(4B) + appId(4B) + imei长度(4B) + body长度(4B) + imei号 + 请求体 */
        // 最小可读长度：28字节 = 7个int字段 * 4字节
        if(in.readableBytes() < 28){
            return;
        }

        Message message = ByteBufToMessageUtils.transition(in);
        if(message == null){
            return;
        }

        out.add(message);
    }
}
