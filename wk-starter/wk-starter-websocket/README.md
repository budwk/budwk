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


