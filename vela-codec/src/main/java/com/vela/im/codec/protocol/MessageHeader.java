package com.vela.im.codec.protocol;

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
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class MessageHeader {

    /** 消息操作指令，十六进制，标识消息类型（登录/心跳/P2P/群聊等） */
    private Integer command;

    /** 协议版本号 */
    private Integer version;

    /** 客户端类型：1-iOS，2-Android，3-Windows，4-Mac，5-Web */
    private Integer clientType;

    /** 应用ID，多租户隔离 */
    private Integer appId;

    /** 数据解析类型：0x0-Json，0x1-Protobuf，0x2-Xml，默认0x0 */
    private Integer messageType = 0x0;

    /** IMEI设备标识长度 */
    private Integer imeiLength;

    /** 包体数据长度 */
    private int length;

    /** 设备唯一标识IMEI号 */
    private String imei;
}
