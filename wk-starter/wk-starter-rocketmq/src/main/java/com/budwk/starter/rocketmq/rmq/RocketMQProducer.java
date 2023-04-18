package com.budwk.starter.rocketmq.rmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.ioc.loader.annotation.IocBean;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class RocketMQProducer {
    private DefaultMQProducer defaultMQProducer;
    private TransactionMQProducer transactionMQProducer;

    private String namesrvAddr;
    private String produceGroup;

    private AtomicInteger transactionIndex = new AtomicInteger(0);

    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<String, Integer>();

    public void close() throws IOException {
        if (this.defaultMQProducer != null) {
            this.defaultMQProducer.shutdown();
            log.info("defaultMQProducer shutdown on {}", namesrvAddr);
        }
        if (this.transactionMQProducer != null) {
            this.transactionMQProducer.shutdown();
            log.info("transactionMQProducer shutdown on {}", namesrvAddr);
        }
    }

    public void init(String namesrvAddr, String produceGroup) throws MQClientException {
        this.namesrvAddr = namesrvAddr;
        this.produceGroup = produceGroup;
        this.initDefaultMQProducer();
        this.initTransactionMQProducer();
    }

    /**
     * 普通生产者
     */
    private DefaultMQProducer initDefaultMQProducer() throws MQClientException {
        this.defaultMQProducer = createDefaultMQProducer(new DefaultMQProducer(produceGroup));
        this.defaultMQProducer.start();
        log.info("defaultMQProducer init on {}", namesrvAddr);
        return this.defaultMQProducer;
    }

    /**
     * 事务生产者
     */
    private TransactionMQProducer initTransactionMQProducer() throws MQClientException {
        this.transactionMQProducer = createTransactionMQProducer(new TransactionMQProducer("TS_" + produceGroup));
        this.transactionMQProducer.start();
        log.info("transactionMQProducer init on {}", namesrvAddr);
        return this.transactionMQProducer;
    }

    private DefaultMQProducer createDefaultMQProducer(DefaultMQProducer producer) {
        producer.setNamesrvAddr(this.namesrvAddr);
        return producer;
    }

    private TransactionMQProducer createTransactionMQProducer(TransactionMQProducer producer) {
        producer.setNamesrvAddr(this.namesrvAddr);
        // 事务消息处理线程池
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 1000 * 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("mq-transaction-msg-check-thread");
                return thread;
            }
        });
        producer.setExecutorService(executorService);
        // 事务监听器
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object o) {
                // 执行生产者方本地事务
                String transId = msg.getTransactionId();
                log.info("LocalTransaction start, transactionId={} %n", transId);
                int value = transactionIndex.getAndIncrement();
                int status = value % 3;
                localTrans.put(transId, status);
                if (status == 0) {
                    // Return local transaction with success(commit), in this case,
                    // this message will not be checked in checkLocalTransaction()
                    log.info("LocalTransaction success transactionId={}", msg.getTransactionId());
                    return LocalTransactionState.COMMIT_MESSAGE;
                }

                if (status == 1) {
                    // Return local transaction with failure(rollback) , in this case,
                    // this message will not be checked in checkLocalTransaction()
                    log.info("LocalTransaction failure transactionId={}", msg.getTransactionId());
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }

                log.info("LocalTransaction unknow transactionId={}", msg.getTransactionId());
                return LocalTransactionState.UNKNOW;
            }

            // 回查本地事务执行情况
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                Integer status = localTrans.get(msg.getTransactionId());
                LocalTransactionState retState = LocalTransactionState.COMMIT_MESSAGE;
                if (null != status) {
                    switch (status) {
                        case 0:
                            retState = LocalTransactionState.COMMIT_MESSAGE;
                            break;
                        case 1:
                            retState = LocalTransactionState.ROLLBACK_MESSAGE;
                            break;
                        case 2:
                            retState = LocalTransactionState.UNKNOW;
                            break;
                    }
                }
                log.info("LocalTransaction recheck, transactionId={}, transactionState={} status={} %n",
                        msg.getTransactionId(), retState, status);
                return retState;
            }
        });
        return producer;
    }

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public TransactionMQProducer getTransactionMQProducer() {
        return transactionMQProducer;
    }
}
