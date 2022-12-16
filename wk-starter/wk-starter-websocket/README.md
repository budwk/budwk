# wk-starter-websocket

* 实现 websocket 通信 和 站内消息 推送

>> 根据部署环境选择实现方式

* 请求路径为  /websocket
* redis 已实现, rabbitmq 未实现

## 配置说明

```yaml
websocket:
  # 实现方式
  type: redis
  # 超时时间(默认3600秒)
  timeout: 3600
```

## 发送消息

```java
@Inject
private MessageSendServer messageSendServer;

// 对用户某个token发送消息
public void wsSendMsg(String userId, String token, String msg) {
    messageSendServer.wsSendMsg(userId, token, msg);
}

// 向某个用户发送消息
public void wsSendMsg(String userId, String msg) {
    messageSendServer.wsSendMsg(userId, msg);
}

// 向一组用户发送消息
public void wsSendMsg(List<String> userLits, String msg) {
    messageSendServer.wsSendMsg(userId, msg);
}

```


