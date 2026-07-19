package com.vela.im.codec.utils;

import com.alibaba.fastjson.JSONObject;
import com.vela.im.codec.protocol.Message;
import com.vela.im.codec.protocol.MessageHeader;
import io.netty.buffer.ByteBuf;

/**
 * <p>Title: ByteBufToMessageUtils</p>
 * <p>Description: ByteBuf 转 Message 工具类，根据私有协议规则解析二进制数据。</p>
 * <p>私有协议规则: 4位command + 4位version + 4位clientType + 4位messageType + 4位appId + 4位imeiLength + imei + 4位bodyLength + body</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class ByteBufToMessageUtils {

    public static Message transition(ByteBuf in){

        /** 获取command*/
        int command = in.readInt();

        /** 获取version*/
        int version = in.readInt();

        /** 获取clientType*/
        int clientType = in.readInt();

        /** 获取clientType*/
        int messageType = in.readInt();

        /** 获取appId*/
        int appId = in.readInt();

        /** 获取imeiLength*/
        int imeiLength = in.readInt();

        /** 获取bodyLen*/
        int bodyLen = in.readInt();

        if(in.readableBytes() < bodyLen + imeiLength){
            in.resetReaderIndex();
            return null;
        }

        byte [] imeiData = new byte[imeiLength];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        byte [] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);


        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setClientType(clientType);
        messageHeader.setCommand(command);
        messageHeader.setLength(bodyLen);
        messageHeader.setVersion(version);
        messageHeader.setMessageType(messageType);
        messageHeader.setImei(imei);

        Message message = new Message();
        message.setMessageHeader(messageHeader);

        if(messageType == 0x0){
            String body = new String(bodyData);
            JSONObject parse = (JSONObject) JSONObject.parse(body);
            message.setMessagePack(parse);
        }

        in.markReaderIndex();
        return message;
    }

}