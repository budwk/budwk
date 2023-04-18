# wk-starter-rocketmq

rocketmq

## 配置说明

```yaml
rocketmq:
  # 是否启用
  nameserver-address: 127.0.0.1:9876
  # 可选,如果无需发送消息则忽略该配置
  producer-group: wk_local_producer
```

## 生产者
```java

```

## 消费者

```java
@RMQConsumer(topic = "MyTopicTest", consumerGroup = "ConsumerGroup1", tag = "TAG1")
public class ConsumerExample1 implements RMQConsumerListener {

    @Override
    public void onMessage(String message) {
        System.out.println("ConsumerExample1 监听到消息==> " + message);
    }
}

@RMQConsumer(topic = "MyTopicTest", consumerGroup = "ConsumerGroup2", tag = "TAG2", messageModel = MessageModel.BROADCASTING)
public class ConsumerExample2 implements RMQConsumerListener {

    @Override
    public void onMessage(String message) {
        System.out.println("ConsumerExample2 监听到消息==> " + message);
    }
}
```