# wk-starter-rocketmq


## 启动 rocketmq

* 启动 nameServer

```text
./mqnamesrv
```

* 启动broker

```text
./mqbroker -n localhost:9876 autoCreateTopicEnable=true --enable-proxy 
```
`autoCreateTopicEnable=true` 自动创建topic

Mac M1 报错需 runserver.sh 增加配置项 `--add-exports=java.base/sun.nio.ch=ALL-UNNAMED`

* 创建topic

```text
$ ./mqadmin updatetopic -n localhost:9876 -t DemoTopic -c DefaultCluster -r 10 -w 10
```
`-r 读队列数 -w 写队列数` 手动创建或通过Web控制台可修改读写数量
## 配置说明

```yaml
rocketmq:
  # 集群环境多个nameserver用;分割
  nameserver-address: 127.0.0.1:9876
  # 生产者组
  producer-group: wk_local_producer
  consumer-thread-max: 0
  consumer-thread-min: 0
```

## 生产者

```java
@Inject
RocketMQServer rocketMQServer;
```

## 消费者

```java
@IocBean
@RMQConsumer(topic = "MyTopicTest", consumerGroup = "ConsumerGroup1", tag = "TAG1")
public class ConsumerExample1 implements RMQConsumerListener {

    @Override
    public void onMessage(String message) {
        System.out.println("ConsumerExample1 监听到消息==> " + message);
    }
}

@IocBean
@RMQConsumer(topic = "MyTopicTest", consumerGroup = "ConsumerGroup2", tag = "TAG2", messageModel = MessageModel.BROADCASTING)
public class ConsumerExample2 implements RMQConsumerListener {

    @Override
    public void onMessage(String message) {
        System.out.println("ConsumerExample2 监听到消息==> " + message);
    }
}
```