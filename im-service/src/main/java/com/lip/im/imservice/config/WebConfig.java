package com.lip.im.imservice.config;

import com.lip.im.imservice.interceptor.GateWayInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: Elon
 * @title: WebConfig
 * @projectName: IM-System
 * @description: TODO
 * @date: 2025/3/6 19:14
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    GateWayInterceptor gateWayInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gateWayInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/v1/user/login")
                .excludePathPatterns("/v1/message/checkSend");
    }

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