package com.budwk.app.device.message;

import com.budwk.starter.rocketmq.enums.ConsumeMode;
import com.budwk.starter.rocketmq.enums.MessageModel;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 消息流转
 *
 * @author wizzer.cn
 */
public interface MessageTransfer {
    /**
     * 发送消息
     *
     * @param mqMessage
     * @param <T>
     */
    <T extends Serializable> void publish(MqMessage<T> mqMessage);

    /**
     * 订阅消息
     *
     * @param consumerGroup 消息组
     * @param topic         订阅主题
     * @param tag           只支持或 || 运算 示例: "tag1 || tag2 || tag3", 等于 null 或者 * ，则表示全部订阅
     * @param messageModel  接收模式(广播模式或集群模式)
     * @param consumeMode   消费模式(顺序或普通)
     * @param consumer      消息对象
     * @param <T>
     */
    <T extends Serializable> void subscribe(String consumerGroup, String topic, String tag, MessageModel messageModel, ConsumeMode consumeMode, Consumer<MqMessage<T>> consumer);
}
