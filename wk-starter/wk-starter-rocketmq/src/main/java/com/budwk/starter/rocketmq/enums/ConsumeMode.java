package com.budwk.starter.rocketmq.enums;

/**
 * @author wizzer.cn
 */
public enum ConsumeMode {
    /**
     * 并发异步接收消息
     */
    CONCURRENTLY("CONCURRENTLY"),

    /**
     * 单线程顺序接收消息
     */
    ORDERLY("ORDERLY");

    private final String mode;

    ConsumeMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return this.mode;
    }
}
