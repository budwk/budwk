package com.budwk.starter.rocketmq.enums;

/**
 * @author wizzer.cn
 */
public enum MessageModel {
    /**
     * 广播模式
     */
    BROADCASTING("BROADCASTING"),
    /**
     * 集群模式(负载均衡)
     */
    CLUSTERING("CLUSTERING");

    private final String model;

    MessageModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return this.model;
    }
}
