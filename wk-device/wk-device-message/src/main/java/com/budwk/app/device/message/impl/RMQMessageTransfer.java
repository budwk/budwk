package com.budwk.app.device.message.impl;

import com.budwk.app.device.message.MessageTransfer;
import com.budwk.app.device.message.MqMessage;
import com.budwk.app.device.message.utils.FastSerializeUtil;
import com.budwk.starter.rocketmq.RocketMQServer;
import com.budwk.starter.rocketmq.enums.ConsumeMode;
import com.budwk.starter.rocketmq.enums.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init", depose = "close")
@Slf4j
public class RMQMessageTransfer implements MessageTransfer {
    @Inject
    private RocketMQServer rocketMQServer;
    @Inject
    private PropertiesProxy conf;

    private String namesrvAddr;
    private int consumeThreadMax = 0;
    private int consumeThreadMin = 0;

    private List<DefaultMQPushConsumer> consumerList = new ArrayList<>();

    public void init() {
        this.namesrvAddr = conf.get(RocketMQServer.PROP_NAMESERVER_ADDRESS);
        this.consumeThreadMax = conf.getInt(RocketMQServer.PROP_CONSUMER_THREAD_MAX);
        this.consumeThreadMin = conf.getInt(RocketMQServer.PROP_CONSUMER_THREAD_MIN);
    }

    @Override
    public <T extends Serializable> void publish(MqMessage<T> mqMessage) {
        try {
            rocketMQServer.sendVoid(mqMessage.getAdress(), FastSerializeUtil.serialize(mqMessage), 0);
        } catch (Exception e) {
            log.error("topic {} 消息发送失败: {}", mqMessage.getAdress(), e.getMessage());
        }
    }

    @Override
    public <T extends Serializable> void subscribe(String consumerGroup, String topic, String tag, MessageModel messageModel, ConsumeMode consumeMode, Consumer<MqMessage<T>> consumer) {
        DefaultMQPushConsumer rmqConsumer = new DefaultMQPushConsumer(consumerGroup);
        try {
            rmqConsumer.setNamesrvAddr(namesrvAddr);
            if (consumeThreadMax > 0) {
                rmqConsumer.setConsumeThreadMax(consumeThreadMax);
            }
            if (consumeThreadMin > 0) {
                rmqConsumer.setConsumeThreadMin(consumeThreadMin);
            }
            rmqConsumer.subscribe(topic, tag);
            switch (messageModel) {
                case BROADCASTING:
                    rmqConsumer.setMessageModel(org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel.BROADCASTING);
                    break;
                case CLUSTERING:
                    rmqConsumer.setMessageModel(org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel.CLUSTERING);
                    break;
                default:
                    throw new IllegalArgumentException("Property 'messageModel' was wrong.");
            }
            if (consumeMode == ConsumeMode.ORDERLY) {
                // 顺序消费
                rmqConsumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
                    for (MessageExt ext : msgs) {
                        log.info("Orderly message, {}", ext);
                        consumer.accept(FastSerializeUtil.deserialize(ext.getBody()));
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                });
            } else {
                // 并发
                rmqConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                    for (MessageExt ext : msgs) {
                        log.info("Concurrently message, {}", ext);
                        consumer.accept(FastSerializeUtil.deserialize(ext.getBody()));
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                });

            }
            consumerList.add(rmqConsumer);
        } catch (MQClientException e) {
            throw new RuntimeException("A consumer as " + topic + " subscribe fail, " + e.getMessage());
        }
    }

    @Override
    public <T extends Serializable> void subscribe(String consumerGroup, String topic, Consumer<MqMessage<T>> consumer) {
        this.subscribe(consumerGroup, topic, "*", MessageModel.CLUSTERING, ConsumeMode.CONCURRENTLY, consumer);
    }

    public void close() {
        for (DefaultMQPushConsumer consumer : consumerList) {
            if (consumer != null) {
                consumer.shutdown();
                log.info("consumer shutdown consumerGroup {}", consumer.getConsumerGroup());
            }
        }
    }
}
