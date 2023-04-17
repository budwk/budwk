# wk-starter-rocketmq

rocketmq

## 配置说明

```yaml
rocketmq:
  # 是否启用
  enable: true
  nameserver-address: 127.0.0.1:9876
  # 可选,如果无需发送消息则忽略该配置
  producer-group: local_producer
  # 发送超时配置毫秒数,可选,默认3000
  #send-timeout: 3000
  # 追溯消息具体消费情况的开关,默认打开
  #trace-enabled: false
  #是否启用VIP通道,默认打开
  #vip-channel-enabled: false
```
