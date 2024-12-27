package com.hua.im.app.server.config;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
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


    /**
     * 实例化连接池,设置连接池管理器,以参数的形式注入上面实例化的连接池管理器
     *
     * @Qualifier 指定bean标签进行注入
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(
            @Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager
    ){

        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        return httpClientBuilder;
    }

}


public CloseableHttpClient getCloseableHttpClient() {
    if (httpClientBuilder != null) {
        return httpClientBuilder.build();
    }
    httpClientBuilder = HttpClientBuilder.create();
    httpClientBuilder.setConnectionManager(getManager());
    return httpClientBuilder.build();
}

/**
 * Builder是RequestConfig的一个内部类 通过RequestConfig的custom方法来获取到一个Builder对象
 * 设置builder的连接信息
 *
 * @return
 */
@Bean(name = "builder")
public RequestConfig.Builder getBuilder() {
    RequestConfig.Builder builder = RequestConfig.custom();
    return builder.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout)
            .setSocketTimeout(socketTimeout).setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
}

/**
 * 使用builder构建一个RequestConfig对象
 *
 * @param builder
 * @return
 */
@Bean
public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
    return builder.build();
}

public Integer getMaxTotal() {
    return maxTotal;
}

public void setMaxTotal(Integer maxTotal) {
    this.maxTotal = maxTotal;
}

public Integer getDefaultMaxPerRoute() {
    return defaultMaxPerRoute;
}

public void setDefaultMaxPerRoute(Integer defaultMaxPerRoute) {
    this.defaultMaxPerRoute = defaultMaxPerRoute;
}

public Integer getConnectTimeout() {
    return connectTimeout;
}

public void setConnectTimeout(Integer connectTimeout) {
    this.connectTimeout = connectTimeout;
}

public Integer getConnectionRequestTimeout() {
    return connectionRequestTimeout;
}

public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
    this.connectionRequestTimeout = connectionRequestTimeout;
}

public Integer getSocketTimeout() {
    return socketTimeout;
}

public void setSocketTimeout(Integer socketTimeout) {
    this.socketTimeout = socketTimeout;
}

public boolean isStaleConnectionCheckEnabled() {
    return staleConnectionCheckEnabled;
}

public void setStaleConnectionCheckEnabled(boolean staleConnectionCheckEnabled) {
    this.staleConnectionCheckEnabled = staleConnectionCheckEnabled;
}

}
