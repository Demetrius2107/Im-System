package com.hua.im.app.server.config;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "httpclient")
public class GlobalHttpClientConfig {

    // 最大连接数
    private Integer maxTotal;

    // 最大并发链接数
    private Integer defaultMaxPerRoute;

    // 创建链接的最大时间
    private Integer connectTimeOut;

    // 链接获取超时时间
    private Integer connectionRequestTimeout;

    // 数据传输最长时间
    private Integer socketTimeout;

    // 提交时检查链接是否可用
    private boolean staleConnectionCheckEnabled;

    PoolingHttpClientConnectionManager manager = null;
    HttpClientBuilder httpClientBuilder = null;

    // 定义httpClient链接池
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(){
        return getManager();
    }

    public PoolingHttpClientConnectionManager getManager(){
        if(manager != null){
            return manager;
        }
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(maxTotal);
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return manager;
    }



}
