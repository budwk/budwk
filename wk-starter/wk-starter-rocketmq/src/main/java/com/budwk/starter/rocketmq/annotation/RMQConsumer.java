package com.budwk.starter.rocketmq.annotation;

import com.budwk.starter.rocketmq.enums.ConsumeMode;
import com.budwk.starter.rocketmq.enums.MessageModel;

import java.lang.annotation.*;

/**
 * 消息消费者
 * @author wizzer.cn
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RMQConsumer {
    String consumerGroup();

    String topic();

    //只支持或 (||) 运算；
    //示例1："tag1 || tag2 || tag3"
    //示例2：subExpression 等于 null 或者 * ，则表示全部订阅
    String tag() default "*";

    MessageModel messageModel() default MessageModel.BROADCASTING;

    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;
}
