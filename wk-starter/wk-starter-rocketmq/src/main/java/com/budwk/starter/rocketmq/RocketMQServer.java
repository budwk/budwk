package com.budwk.starter.rocketmq;

import com.budwk.starter.rocketmq.enums.DelayTimeLevel;
import com.budwk.starter.rocketmq.rmq.RocketMQConsumer;
import com.budwk.starter.rocketmq.rmq.RocketMQProducer;
import com.budwk.starter.rocketmq.utils.MessagesSplitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init", depose = "close")
@Slf4j
public class RocketMQServer {
    @Inject
    private PropertiesProxy conf;

    public static final String PRE = "rocketmq.";

    @PropDoc(value = "RocketMQ 是否启用", defaultValue = "true", type = "boolean")
    public static final String PROP_ENABLE = PRE + "enable";

    @PropDoc(value = "RocketMQ 服务地址", defaultValue = "")
    public static final String PROP_NAMESERVER_ADDRESS = PRE + "nameserver-address";

    @PropDoc(value = "RocketMQ 生产者组", defaultValue = "")
    public static final String PROP_PRODUCER_GROUP = PRE + "producer-group";

    @PropDoc(value = "RocketMQ 消费者最大线程数", defaultValue = "")
    public static final String PROP_CONSUMER_THREAD_MAX = PRE + "consumer-thread-max";

    @PropDoc(value = "RocketMQ 消费者最小线程数", defaultValue = "")
    public static final String PROP_CONSUMER_THREAD_MIN = PRE + "consumer-thread-min";

    @Inject
    private RocketMQProducer rmqProducer;
    @Inject
    private RocketMQConsumer rmqConsumer;

    private DefaultMQProducer defaultMQProducer;
    private TransactionMQProducer transactionMQProducer;

    public void init() throws MQClientException {
        rmqProducer.init(conf.get(PROP_NAMESERVER_ADDRESS), conf.get(PROP_PRODUCER_GROUP));
        rmqConsumer.init(conf.get(PROP_NAMESERVER_ADDRESS), conf.getInt(PROP_CONSUMER_THREAD_MAX, 0), conf.getInt(PROP_CONSUMER_THREAD_MIN, 0));
        defaultMQProducer = rmqProducer.getDefaultMQProducer();
        transactionMQProducer = rmqProducer.getTransactionMQProducer();
    }

    public void close() throws IOException {
        rmqProducer.close();
        rmqConsumer.close();
    }

    public SendResult send(String topic, String msg, long timeout) throws Exception {
        return this.send(topic, "", msg, timeout);
    }

    public SendResult send(String topic, String tags, String msg, long timeout) throws Exception {
        return this.send(topic, tags, "", msg, DelayTimeLevel.ZERO, timeout);
    }

    public SendResult send(String topic, String tags, String keys, String msg, DelayTimeLevel delayTimeLevel, long timeout) throws Exception {
        return this.sendMsg(topic, tags, keys, msg, null, false, delayTimeLevel, timeout);
    }

    public void sendAsync(String topic, String msg, SendCallback sendCallback, long timeout) throws Exception {
        this.sendAsync(topic, "", msg, sendCallback, timeout);
    }

    public void sendAsync(String topic, String tags, String msg, SendCallback sendCallback, long timeout) throws Exception {
        this.sendAsync(topic, tags, "", msg, sendCallback, DelayTimeLevel.ZERO, timeout);
    }

    public void sendAsync(String topic, String tags, String keys, String msg, SendCallback sendCallback, DelayTimeLevel delayTimeLevel, long timeout) throws Exception {
        this.sendMsg(topic, tags, keys, msg, sendCallback, false, delayTimeLevel, timeout);
    }

    public void sendOneway(String topic, String msg, long timeout) throws Exception {
        this.sendOneway(topic, "", msg, timeout);
    }

    public void sendOneway(String topic, String tags, String msg, long timeout) throws Exception {
        this.sendOneway(topic, tags, "", msg, DelayTimeLevel.ZERO, timeout);
    }

    public void sendOneway(String topic, String tags, String keys, String msg, DelayTimeLevel delayTimeLevel, long timeout) throws Exception {
        this.sendMsg(topic, tags, keys, msg, null, true, delayTimeLevel, timeout);
    }

    public SendResult send(String topic, String tags, String msg, MessageQueueSelector selector, Object arg) throws Exception {
        return this.sendMsg(topic, tags, "", msg, null, false, DelayTimeLevel.ZERO, 0, selector, arg);
    }

    /**
     * 批量消息，注意最大4MB
     */
    public void sendBatch(List<Message> msgs) throws Exception {
        MessagesSplitter messagesSplitter = new MessagesSplitter(msgs);
        while (messagesSplitter.hasNext()) {
            List<Message> subMessageList = messagesSplitter.next();
            SendResult sendResult = this.defaultMQProducer.send(subMessageList);
            log.info("消息发送状态: {}, size,{}", sendResult, sendResult.getMsgId().split(",").length);
        }
    }

    private Message createMessage(String topic, String tags, String keys, String msg, SendCallback sendCallback, boolean isOneway,
                                  DelayTimeLevel delayTimeLevel, long timeout) {
        Message message = new Message(topic, tags, keys, msg.getBytes(StandardCharsets.UTF_8));
        message.setDelayTimeLevel(delayTimeLevel.getLevel());
        if (timeout > 0) {
            message.setDeliverTimeMs(timeout);
        }
        return message;
    }

    /**
     * 发送消息
     *
     * @param topic          主题
     * @param tags           过滤标签Tag
     * @param keys           索引Key列表
     * @param sendCallback   异步回调
     * @param isOneway       是否单向发送
     * @param delayTimeLevel 延时消息等级，大于0才起作用，级别： 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * @param timeout        定时时间戳，单位毫秒
     */
    private SendResult sendMsg(String topic, String tags, String keys, String msg, SendCallback sendCallback, boolean isOneway,
                               DelayTimeLevel delayTimeLevel, long timeout) throws Exception {
        Message message = createMessage(topic, tags, keys, msg, sendCallback, isOneway, delayTimeLevel, timeout);
        log.info("监听到消息发送, topic={}, tags={}, keys={}, msg={}, isOneway={}, delayTimeLevel={}, timeout={}",
                topic, tags, keys, msg, isOneway, delayTimeLevel, timeout);
        if (isOneway) {
            // 单向发送,由于在oneway方式发送消息时没有请求应答处理，如果出现消息发送失败，则会因为没有重试而导致数据丢失。若数据不可丢，建议选用可靠同步或可靠异步发送方式。
            this.defaultMQProducer.sendOneway(message);
        } else {
            if (sendCallback == null) {
                // 同步发送消息，只要不抛异常就是成功。
                return this.defaultMQProducer.send(message);
            } else {
                // 异步发送
                this.defaultMQProducer.send(message, sendCallback);
            }
        }
        return null;
    }

    /**
     * @param selector 消息队列选择器，根据业务唯一标识自定义队列选择算法
     * @param arg      选择队列的业务标识
     */
    private SendResult sendMsg(String topic, String tags, String keys, String msg, SendCallback sendCallback, boolean isOneway,
                               DelayTimeLevel delayTimeLevel, long timeout, MessageQueueSelector selector, Object arg) throws Exception {
        Message message = createMessage(topic, tags, keys, msg, sendCallback, isOneway, delayTimeLevel, timeout);
        log.info("监听到消息发送, message={}", message);
        return this.defaultMQProducer.send(message, selector, arg);
    }


    /**
     * 发送事务消息
     */
    public TransactionSendResult sendTransactionMsg(Message message) throws Exception {
        return this.transactionMQProducer.sendMessageInTransaction(message, null);
    }

}
