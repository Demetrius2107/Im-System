package com.vela.im.service.infrastructure.config;

import com.vela.im.service.application.utils.SnowflakeIdWorker;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.types.enums.ImUrlRouteWayEnum;
import com.vela.im.shared.types.enums.RouteHashMethodEnum;
import com.vela.im.shared.route.RouteHandle;
import com.vela.im.shared.route.algorithm.consistenthash.AbstractConsistentHash;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * <p>Title: BeanConfig</p>
 * <p>Description: Bean 配置类，初始化 ZkClient、路由策略等基础设施 Bean。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-06
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Configuration
public class BeanConfig {

    private final AppConfig appConfig;

    public BeanConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    /**
     * 构建 ZooKeeper 客户端连接
     *
     * @return ZkClient 实例
     */
    @Bean
    public ZkClient buildZKClient(){
        return new ZkClient(appConfig.getZkAddr(),appConfig.getZkConnectTimeOut());
    }


    /**
     * 构建路由策略处理器
     * <p>支持轮询、随机、一致性哈希三种路由算法。</p>
     *
     * @return RouteHandle 路由处理器实例
     * @throws Exception 反射创建路由算法实例失败时抛出
     */
    @Bean
    public RouteHandle routeHandle() throws Exception {

        Integer imRouteWay = appConfig.getImRouteWay();
        String routWay = "";

        ImUrlRouteWayEnum handler = ImUrlRouteWayEnum.getHandler(imRouteWay);
        routWay = handler.getClazz();

        RouteHandle routeHandle = (RouteHandle) Class.forName(routWay).newInstance();
        if(handler == ImUrlRouteWayEnum.HASH){

            Method setHash = Class.forName(routWay).getMethod("setHash", AbstractConsistentHash.class);
            Integer consistentHashWay = appConfig.getConsistentHashWay();
            String hashWay = "";

            RouteHashMethodEnum hashHandler = RouteHashMethodEnum.getHandler(consistentHashWay);
            hashWay = hashHandler.getClazz();
            AbstractConsistentHash consistentHash
                    = (AbstractConsistentHash) Class.forName(hashWay).newInstance();
            setHash.invoke(routeHandle,consistentHash);
        }

        return routeHandle;
    }

    @Bean
    public EasySqlInjector easySqlInjector () {
        return new EasySqlInjector();
    }

    @Bean
    public SnowflakeIdWorker buildSnowflakeSeq() throws Exception {
        return new SnowflakeIdWorker(0);
    }

}
