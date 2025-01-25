package config;

import lombok.Data;

/**
 * @author: Elon
 * @title: BootStrapConfig
 * @projectName: im-system
 * @description: TODO
 * @date: 2025/1/24 21:22
 */
@Data
public class BootStrapConfig {

    private TcpConfig lim;

    @Data
    public static class TcpConfig {

        // tcp绑定的端口号
        private Integer tcpPort;

        // websocket绑定的端口号
        private Integer webSocketPort;

        // 是否启用websocket
        private boolean enableWebSocket;

        // boss线程 默认=1
        private Integer boosThreadSize;

        // work线程
        private Integer workThreadSize;

        // 心跳超时时间 单位毫秒
        private Long heartBeatTime;

        // 登录系统
        private Integer loginModel;

        private Integer brokerId;

        private String logicUrl;

    }


}
