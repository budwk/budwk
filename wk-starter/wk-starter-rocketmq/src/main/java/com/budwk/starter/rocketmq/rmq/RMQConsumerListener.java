package com.budwk.starter.rocketmq.rmq;

/**
 * @author wizzer.cn
 */
public interface RMQConsumerListener {
    void onMessage(byte[] message);
}
