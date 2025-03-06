package com.lip.im.imservice.config;

import com.lip.im.imservice.utils.SnowflakeIdWorker;
import com.lip.im.model.config.AppConfig;
import com.lip.im.model.enums.ImUrlRouteWayEnum;
import com.lip.im.model.enums.RouteHashMethodEnum;
import com.lip.im.model.route.RouteHandle;
import com.lip.im.model.route.algorithm.consistenthash.AbstractConsistentHash;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

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
