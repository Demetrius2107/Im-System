package com.vela.im.tcp.infrastructure.register;

import com.vela.im.codec.config.BootStrapConfig;
import com.vela.im.shared.constants.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Title: RegistryZK</p>
 * <p>Description: ZooKeeper 注册器，将本机 TCP/WebSocket 服务地址注册到 ZK 指定路径</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-05
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Slf4j
public class RegistryZK implements Runnable {

    /** ZK 客户端工具 */
    private final Zkit zkit;

    /** 本机 IP 地址 */
    private final String ip;

    /** TCP 服务配置 */
    private final BootStrapConfig.ServerConfig tcpConfig;

    /**
     * 构造 ZK 注册器
     *
     * @param zkit     ZK 客户端工具
     * @param ip       本机 IP 地址
     * @param tcpConfig TCP 服务配置
     */
    public RegistryZK(Zkit zkit, String ip, BootStrapConfig.ServerConfig tcpConfig) {
        this.zkit = zkit;
        this.ip = ip;
        this.tcpConfig = tcpConfig;
    }

    /**
     * 向 ZK 注册 TCP 和 WebSocket 服务节点路径
     */
    @Override
    public void run() {
        zkit.createRootNode();
        String tcpPath = Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp + "/" + ip + ":" + tcpConfig.getTcpPort();
        zkit.createNode(tcpPath);
        log.info("Registry zookeeper tcpPath success, msg=[{}]", tcpPath);

        String webPath =
                Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb + "/" + ip + ":" + tcpConfig.getWebSocketPort();
        zkit.createNode(webPath);
        log.info("Registry zookeeper webPath success, msg=[{}]", tcpPath);

    }
}