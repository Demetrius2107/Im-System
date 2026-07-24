package com.vela.im.service.interfaces.interceptor;

import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.trace.TraceIdContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: TraceIdInterceptor</p>
 * <p>Description: Spring MVC 拦截器，从 HTTP Header 中读取 TraceId 并写入 MDC。</p>
 * <p>如果上游未传递，则在网关入口自动生成，保证每个请求都有链路 ID。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-22
 * @updateTime 2026-07-22
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(Constants.TraceId.HTTP_HEADER_NAME);
        TraceIdContext.set(traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求处理结束后清理，避免线程池复用导致上下文污染
        TraceIdContext.clear();
    }
}
