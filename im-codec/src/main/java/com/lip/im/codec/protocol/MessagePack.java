package com.lip.im.codec.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Title: MessagePack</p>
 * <p>Description: 消息封装体，泛型容器，携带userId/appId/clientType/imei/command等路由信息及业务数据data。</p>
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
public class MessagePack<T> implements Serializable {

    /** 用户ID，标识消息的发送方或接收方 */
    private String userId;

    /** 应用ID，多租户隔离标识 */
    private Integer appId;

    /** 接收方ID */
    private String toId;

    /** 客户端类型：1-iOS，2-Android，3-Windows，4-Mac，5-Web */
    private int clientType;

    /** 消息ID，全局唯一，用于去重和ACK */
    private String messageId;

    /** 客户端设备唯一标识IMEI */
    private String imei;

    /** 消息指令，标识消息类型（登录/心跳/P2P/群聊等） */
    private Integer command;

    /** 业务数据对象，聊天消息透传，不参与协议解析 */
    private T data;

    /** 用户签名，用于接口鉴权 */
    private String userSign;

}
