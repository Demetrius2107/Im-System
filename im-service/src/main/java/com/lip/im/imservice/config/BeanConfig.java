package com.lip.im.imservice.config;

import com.lip.im.model.config.AppConfig;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Elon
 * @title: BeanConfig
 * @projectName: IM-System
 * @description: Bean配置类
 * @date: 2025/3/6 19:14
 */
@Configuration
public class BeanConfig {

    @Autowired
    AppConfig appConfig;

    @Bean
    public ZkClient buildZKClient(){
        return new ZkClient(appConfig.getZkAddr(),appConfig.getZkConnectTimeOut());
    }

}
