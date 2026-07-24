package com.vela.im.shared.trace;

import com.vela.im.shared.constants.Constants;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

/**
 * <p>Title: TraceIdContext</p>
 * <p>Description: 全链路 TraceId 上下文工具类，基于 SLF4J MDC 实现。</p>
 * <p>说明：MDC 本质是线程绑定的日志上下文，本类负责 TraceId 的生成、设置、透传和清理。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-22
 * @updateTime 2026-07-22
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class TraceIdContext {

    /**
     * 生成新的 TraceId
     * 格式：时间戳前缀 + UUID 去重，保证可读性与唯一性
     *
     * @return 新的 TraceId
     */
    public static String generate() {
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 获取当前线程的 TraceId，不存在则返回 null
     *
     * @return 当前 TraceId
     */
    public static String get() {
        return MDC.get(Constants.TraceId.TRACE_ID_KEY);
    }

    /**
     * 设置 TraceId 到当前线程 MDC
     * 如果传入为空，则自动生成一个新的
     *
     * @param traceId 链路 ID
     */
    public static void set(String traceId) {
        if (traceId == null || traceId.isEmpty()) {
            traceId = generate();
        }
        MDC.put(Constants.TraceId.TRACE_ID_KEY, traceId);
    }

    /**
     * 清空当前线程的 TraceId
     */
    public static void clear() {
        MDC.remove(Constants.TraceId.TRACE_ID_KEY);
    }

    /**
     * 获取当前线程 MDC 的快照，用于向子线程透传
     *
     * @return MDC 上下文快照
     */
    public static Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * 将快照设置到当前线程 MDC
     *
     * @param contextMap MDC 上下文快照
     */
    public static void setContextMap(Map<String, String> contextMap) {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
    }

    /**
     * 从 AMQP 消息头中解析 TraceId 并设置到当前线程 MDC
     *
     * @param headers AMQP 消息头
     */
    public static void setFromAmqpHeaders(Map<String, Object> headers) {
        if (headers == null) {
            set(null);
            return;
        }
        Object traceId = headers.get(Constants.TraceId.MQ_HEADER_NAME);
        set(traceId != null ? traceId.toString() : null);
    }

    /**
     * 包装 Runnable，使其在子线程中也能携带 TraceId
     *
     * @param runnable 原始任务
     * @return 包装后的任务
     */
    public static Runnable wrap(Runnable runnable) {
        Map<String, String> contextMap = getCopyOfContextMap();
        return () -> {
            setContextMap(contextMap);
            try {
                runnable.run();
            } finally {
                clear();
            }
        };
    }
}
