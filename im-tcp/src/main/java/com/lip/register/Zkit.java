package com.lip.register;

import com.lip.constants.Constants;
import org.I0Itec.zkclient.ZkClient;

/**
 * @author: Elon
 * @title: Zkit
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/5 21:25
 */
public class Zkit {

    private ZkClient zkClient;

    // im-coreRoot/rcp/ip:root
    public void createRootNode() {

        boolean exists = zkClient.exists(Constants.ImCoreZkRoot);
        if (!exists) {
            zkClient.createPersistent(Constants.ImCoreZkRoot);
        }

        boolean tcpExists = zkClient.exists(Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp);
        if (!tcpExists) {
            zkClient.createPersistent(Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp);
        }

        boolean webExists = zkClient.exists(Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb);
        if (!webExists) {
            zkClient.createPersistent(Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb);
        }
    }

    // ip + port;
    public void createNode(String path) {
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path);
        }
    }
}
