package com.vela.im.service.infrastructure.config;

import com.vela.im.service.interfaces.interceptor.GateWayInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>Title: WebConfig</p>
 * <p>Description: Web MVC 配置类，注册拦截器、配置跨域（CORS）策略。</p>
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
public class WebConfig implements WebMvcConfigurer {

    private final GateWayInterceptor gateWayInterceptor;

    public WebConfig(GateWayInterceptor gateWayInterceptor) {
        this.gateWayInterceptor = gateWayInterceptor;
    }

    /**
     * 注册拦截器，排除登录和发送校验接口
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gateWayInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/v1/user/login")
                .excludePathPatterns("/v1/message/checkSend");
    }

    /**
     * 配置跨域（CORS）全局策略
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }

}