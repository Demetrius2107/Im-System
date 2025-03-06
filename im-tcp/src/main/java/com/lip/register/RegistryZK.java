package com.lip.register;

import com.lip.config.BootStrapConfig;
import com.lip.im.model.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Elon
 * @title: RegistryZK
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/5 21:32
 */
public class RegistryZK implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RegistryZK.class);

    private Zkit zkit;

    private String ip;

    private BootStrapConfig.TcpConfig tcpConfig;

    public RegistryZK(Zkit zkit, String ip, BootStrapConfig.TcpConfig tcpConfig) {
        this.zkit = zkit;
        this.ip = ip;
        this.tcpConfig = tcpConfig;
    }

    @Override
    public void run() {
        zkit.createRootNode();
        String tcpPath = Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp + "/" + ip + ":" + tcpConfig.getTcpPort();
        zkit.createNode(tcpPath);
        logger.info("Registry zookeeper tcpPath success, msg=[{}]", tcpPath);

        String webPath =
                Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb + "/" + ip + ":" + tcpConfig.getWebSocketPort();
        zkit.createNode(webPath);
        logger.info("Registry zookeeper webPath success, msg=[{}]", tcpPath);

    }
}
