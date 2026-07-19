package com.vela.im.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>Title: AppConfig</p>
 * <p>Description: 应用配置类，映射 application.yml 中 appconfig 前缀的配置项，包含路由/回调/多端登录等开关。</p>
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
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {

    /** 私钥，用于用户签名校验 */
    private String privateKey;

    /** ZooKeeper连接地址 */
    private String zkAddr;

    /** ZooKeeper连接超时时间（毫秒） */
    private Integer zkConnectTimeOut;

    /** 路由策略：1-轮询，2-随机，3-一致性哈希 */
    private Integer imRouteWay;

    /** 一致性哈希算法：1-TreeMap，2-自定义Map */
    private Integer consistentHashWay;

    /** TCP端口 */
    private Integer tcpPort;

    /** WebSocket端口 */
    private Integer webSocketPort;

    /** 是否需要开启WebSocket */
    private Boolean needWebSocket;

    /** 登录模式：1-单端，2-双端，3-多端，4-不限制 */
    private Integer loginModel;

    /** 消息可撤回时间（毫秒），默认1200000ms */
    private Long messageRecallTimeOut;

    /** 群最大成员数量 */
    private Integer groupMaxMemberCount;

    /** 发送消息是否校验关系链 */
    private boolean sendMessageCheckFriend;

    /** 发送消息是否校验黑名单 */
    private boolean sendMessageCheckBlack;

    /** 回调URL */
    private String callbackUrl;

    /** 用户资料变更后回调开关 */
    private boolean modifyUserAfterCallback;

    /** 添加好友后回调开关 */
    private boolean addFriendAfterCallback;

    /** 添加好友前回调开关 */
    private boolean addFriendBeforeCallback;

    /** 修改好友后回调开关 */
    private boolean modifyFriendAfterCallback;

    /** 删除好友后回调开关 */
    private boolean deleteFriendAfterCallback;

    /** 添加黑名单后回调开关 */
    private boolean addFriendShipBlackAfterCallback;

    /** 删除黑名单后回调开关 */
    private boolean deleteFriendShipBlackAfterCallback;

    /** 创建群聊后回调开关 */
    private boolean createGroupAfterCallback;

    /** 修改群聊后回调开关 */
    private boolean modifyGroupAfterCallback;

    /** 解散群聊后回调开关 */
    private boolean destroyGroupAfterCallback;

    /** 删除群成员后回调开关 */
    private boolean deleteGroupMemberAfterCallback;

    /** 拉人入群前回调开关 */
    private boolean addGroupMemberBeforeCallback;

    /** 拉人入群后回调开关 */
    private boolean addGroupMemberAfterCallback;

    /** 发送单聊消息后回调开关 */
    private boolean sendMessageAfterCallback;

    /** 发送单聊消息前回调开关 */
    private boolean sendMessageBeforeCallback;

    /** 会话删除同步模式：1-多端同步 */
    private Integer deleteConversationSyncMode;

    /** 离线消息最大存储条数 */
    private Integer offlineMessageCount;

}
