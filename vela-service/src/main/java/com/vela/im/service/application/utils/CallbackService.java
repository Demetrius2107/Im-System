package com.vela.im.service.application.utils;

import com.vela.im.shared.base.Result;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.utils.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: CallbackService</p>
 * <p>Description: HTTP 回调服务，异步调用外部回调接口，支持前置和后置回调。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-06
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Component
public class CallbackService {

    private static final Logger logger = LoggerFactory.getLogger(CallbackService.class);

    private final HttpRequestUtils httpRequestUtils;
    private final AppConfig appConfig;
    private final ShareThreadPool shareThreadPool;

    public CallbackService(HttpRequestUtils httpRequestUtils,
                           AppConfig appConfig,
                           ShareThreadPool shareThreadPool) {
        this.httpRequestUtils = httpRequestUtils;
        this.appConfig = appConfig;
        this.shareThreadPool = shareThreadPool;
    }

    /**
     * 之后回调
     * @param appId
     * @param callbackCommand
     * @param jsonBody
     */
    public void callback(Integer appId,String callbackCommand,String jsonBody){
        shareThreadPool.submit(() -> {
            try {
                httpRequestUtils.doPost(appConfig.getCallbackUrl(),Object.class,builderUrlParams(appId,callbackCommand),
                        jsonBody,null);
            }catch (Exception e){
                logger.error("callback 回调{} : {}出现异常 ： {} ",callbackCommand , appId, e.getMessage());
            }
        });
    }

    /**
     * 之前回调
     * @param appId
     * @param callbackCommand
     * @param jsonBody
     * @return
     */
    public Result beforeCallback(Integer appId,String callbackCommand,String jsonBody){
        try {
            Result responseVO = httpRequestUtils.doPost("", Result.class, builderUrlParams(appId, callbackCommand),
                    jsonBody, null);
            return responseVO;
        }catch (Exception e){
            logger.error("callback 之前 回调{} : {}出现异常 ： {} ",callbackCommand , appId, e.getMessage());
            return Result.ok();
        }
    }

    public Map builderUrlParams(Integer appId, String command) {
        Map map = new HashMap();
        map.put("appId", appId);
        map.put("command", command);
        return map;
    }

}
