package com.lip.im.codec.protocol;

import lombok.Data;

/**
 * <p>Title: MessageHeader</p>
 * <p>Description: 消息协议头，包含指令(command)、版本(version)、端类型(clientType)、消息解析类型(messageType)、</p>
 * <p>appId、imei长度(imeiLength)、body长度(bodyLength)及imei号。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2025-03-06
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class MessageHeader {

    // 消息操作指令 十六进制 一个消息的开始通常以0x开头
    // 4字节
    private Integer command;
    // 4字节 版本号
    private Integer version;
    // 4字节 端类型
    private Integer clientType;

    /**
     * 应用ID
     */
    // 4字节 appId
    private Integer appId;

    /**
     * 数据解析类型 和具体业务无关，后续根据解析类型解析data数据 0x0:Json,0x1:ProtoBuf,0x2:Xml,默认:0x0
     */
    // 4字节 解析类型
    private Integer messageType = 0x0;

    // 4字节 imel长度
    private Integer imeiLength;

    // 4字节 包体长度
    private int length;

    // imei号
    private String imei;
}
