package com.vela.im.service.application.utils;

import com.alibaba.fastjson.JSON;
import com.vela.im.shared.constants.Constants;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>Title: ZKit</p>
 * <p>Description: ZooKeeper 工具类，查询可用的 TCP/WebSocket 网关节点列表。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-06
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Component
public class ZKit {

    private static final Logger logger = LoggerFactory.getLogger(ZKit.class);

    private final ZkClient zkClient;

    public ZKit(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * get all TCP server node from zookeeper
     *
     * @return
     */
    public List<String> getAllTcpNode() {
        List<String> children = zkClient.getChildren(Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp);
        logger.info("Query all node =[{}] success.", JSON.toJSONString(children));
        return children;
    }

    /**
     * get all WEB server node from zookeeper
     *
     * @return
     */
    public List<String> getAllWebNode() {
        List<String> children = zkClient.getChildren(Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb);
        logger.info("Query all node =[{}] success.", JSON.toJSONString(children));
        return children;
    }

}
