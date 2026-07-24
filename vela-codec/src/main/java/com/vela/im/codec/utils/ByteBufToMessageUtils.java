package com.vela.im.codec.utils;

import com.alibaba.fastjson.JSONObject;
import com.vela.im.codec.protocol.Message;
import com.vela.im.codec.protocol.MessageHeader;
import io.netty.buffer.ByteBuf;

/**
 * <p>Title: ByteBufToMessageUtils</p>
 * <p>Description: ByteBuf 解码工具类，根据私有协议规则将二进制数据解码为 Message 对象。</p>
 * <p>私有协议规则（定长包头 + 变长包体）:</p>
 * <pre>
 *   [4B command][4B version][4B clientType][4B messageType]
 *   [4B appId][4B imeiLength][imei][4B bodyLength][body]
 * </pre>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-24
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class ByteBufToMessageUtils {

    /**
     * 从 ByteBuf 中解码出一条完整的消息。
     * <p>先读取定长包头（28字节），校验包体完整性后读取 imei 和 body，
     * 组装为 {@link Message} 对象返回。若数据不完整则回退读指针并返回 null。</p>
     *
     * @param in Netty 接收缓冲区，必须包含至少 28 字节的包头数据
     * @return 解码后的 Message 对象，数据不完整时返回 null
     * @throws IndexOutOfBoundsException 若 ByteBuf 可读字节不足 28 字节（包头长度）
     */
    public static Message decode(ByteBuf in) {

        // 读取定长包头（7个int = 28字节）
        int command     = in.readInt(); // 指令类型
        int version     = in.readInt(); // 协议版本号
        int clientType  = in.readInt(); // 客户端类型
        int messageType = in.readInt(); // 消息解析类型（0x0 = JSON）
        int appId       = in.readInt(); // 应用ID
        int imeiLength  = in.readInt(); // IMEI 长度
        int bodyLength  = in.readInt(); // 消息体长度

        // 边界校验：IMEI/body 长度必须为正数，且缓冲区余量充足
        if (imeiLength < 0 || bodyLength < 0
                || in.readableBytes() < imeiLength + bodyLength) {
            in.resetReaderIndex();
            return null;
        }

        // 读取 IMEI
        byte[] imeiBytes = new byte[imeiLength];
        in.readBytes(imeiBytes);
        String imei = new String(imeiBytes);

        // 读取消息体
        byte[] bodyBytes = new byte[bodyLength];
        in.readBytes(bodyBytes);

        // 组装消息头
        MessageHeader header = new MessageHeader();
        header.setAppId(appId);
        header.setClientType(clientType);
        header.setCommand(command);
        header.setLength(bodyLength);
        header.setVersion(version);
        header.setMessageType(messageType);
        header.setImei(imei);

        // 组装消息
        Message message = new Message();
        message.setMessageHeader(header);

        // JSON 类型消息体直接解析
        if (messageType == 0x0) {
            String body = new String(bodyBytes);
            JSONObject bodyJson = JSONObject.parseObject(body);
            message.setMessagePackage(bodyJson);
        }

        in.markReaderIndex();
        return message;
    }

}