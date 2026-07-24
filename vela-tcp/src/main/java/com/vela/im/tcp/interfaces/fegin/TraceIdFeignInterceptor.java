package com.vela.im.tcp.interfaces.fegin;

import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.trace.TraceIdContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * <p>Title: TraceIdFeignInterceptor</p>
 * <p>Description: Feign 请求拦截器，将当前线程 MDC 中的 TraceId 透传到 HTTP Header。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-22
 * @updateTime 2026-07-22
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class TraceIdFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String traceId = TraceIdContext.get();
        if (traceId != null && !traceId.isEmpty()) {
            template.header(Constants.TraceId.HTTP_HEADER_NAME, traceId);
        }
    }
}
