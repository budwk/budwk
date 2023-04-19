package com.budwk.starter.rocketmq.rmq;

import com.budwk.starter.rocketmq.annotation.RMQConsumer;
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
import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class RocketMQConsumer {
    @Inject("refer:$ioc")
    protected Ioc ioc;

    private String namesrvAddr;
    private int consumeThreadMax = 0;
    private int consumeThreadMin = 0;
    private List<DefaultMQPushConsumer> consumerList = new ArrayList<>();

    public void init(String namesrvAddr,int consumeThreadMax,int consumeThreadMin) {
        this.namesrvAddr = namesrvAddr;
        this.consumeThreadMax = consumeThreadMax;
        this.consumeThreadMin = consumeThreadMin;
        for (String name : ioc.getNamesByAnnotation(RMQConsumer.class)) {
            RMQConsumerListener listener = ioc.get(RMQConsumerListener.class, name);
            RMQConsumer rmqConsumer = listener.getClass().getAnnotation(RMQConsumer.class);
            DefaultMQPushConsumer consumer = createRocketMQConsumer(listener, rmqConsumer);
            try {
                consumer.start();
                log.info("A consumer as {} topic:{},consumerGroup:{},tag:{} init on namesrc {}", listener.getClass().getSimpleName(), rmqConsumer.topic(), rmqConsumer.consumerGroup(), rmqConsumer.tag(), namesrvAddr);
            } catch (MQClientException e) {
                throw new RuntimeException("A consumer as " + listener.getClass().getSimpleName() + " start fail, " + e.getMessage());
            }
        }
    }

    private DefaultMQPushConsumer createRocketMQConsumer(RMQConsumerListener listener, RMQConsumer annotation) {
        String topic = annotation.topic();
        String consumerGroup = annotation.consumerGroup();

        String tag = annotation.tag();
        ConsumeMode consumeMode = annotation.consumeMode();
        MessageModel messageModel = annotation.messageModel();

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        if(consumeThreadMax>0) {
            consumer.setConsumeThreadMax(consumeThreadMax);
        }
        if(consumeThreadMin>0) {
            consumer.setConsumeThreadMin(consumeThreadMin);
        }
        try {
            consumer.subscribe(topic, tag);
            switch (messageModel) {
                case BROADCASTING:
                    consumer.setMessageModel(org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel.BROADCASTING);
                    break;
                case CLUSTERING:
                    consumer.setMessageModel(org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel.CLUSTERING);
                    break;
                default:
                    throw new IllegalArgumentException("Property 'messageModel' was wrong.");
            }

            if (consumeMode == ConsumeMode.ORDERLY) {
                // 顺序消费
                consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
                    for (MessageExt ext : msgs) {
                        log.info("Orderly message, {}", ext);
                        listener.onMessage(new String(ext.getBody(), StandardCharsets.UTF_8));
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                });
            } else {
                // 并发
                consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                    for (MessageExt ext : msgs) {
                        log.info("Concurrently message, {}", ext);
                        listener.onMessage(new String(ext.getBody(), StandardCharsets.UTF_8));
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                });

            }
        } catch (MQClientException e) {
            throw new RuntimeException("A consumer as " + listener.getClass().getSimpleName() + " registerBean fail, " + e.getMessage());
        }
        consumerList.add(consumer);
        return consumer;
    }

    public void close() {
        for (DefaultMQPushConsumer consumer : consumerList) {
            consumer.shutdown();
            log.info("consumer shutdown consumerGroup {}", consumer.getConsumerGroup());
        }
    }
}
