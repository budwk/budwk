package com.budwk.app;

import com.budwk.starter.rocketmq.annotation.RMQConsumer;
import com.budwk.starter.rocketmq.enums.ConsumeMode;
import com.budwk.starter.rocketmq.enums.MessageModel;
import com.budwk.starter.rocketmq.rmq.RMQConsumerListener;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@Slf4j
@IocBean
@RMQConsumer(topic = "DemoTopic",consumerGroup = "T3",messageModel = MessageModel.CLUSTERING)
public class Consumer01 implements RMQConsumerListener {
    @Override
    public void onMessage(String message) {
        log.debug("Consumer01:::"+message);
    }
}
