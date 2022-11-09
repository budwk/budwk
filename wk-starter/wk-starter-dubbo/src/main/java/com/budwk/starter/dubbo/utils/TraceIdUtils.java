package com.budwk.starter.dubbo.utils;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author wizzer@qq.com
 */
public class TraceIdUtils {
    private static final ThreadLocal<String> traceIdCache = new TransmittableThreadLocal<>();

    public static String getTraceId() {
        return traceIdCache.get();
    }

    public static void setTraceId(String traceId) {
        traceIdCache.set(traceId);
    }

    public static void clear() {
        traceIdCache.remove();
    }
}
