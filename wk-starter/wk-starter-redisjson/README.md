# wk-starter-redisjson

Redis 需安装 redisjson 插件

redis 连接配置和之前一样

```yaml
redis:
  host: 127.0.0.1
  port: 6379
  timeout: 2000
  max_redir: 10
  database: 0
  maxTotal: 100
  maxIdle: 10
  #password: 123
  # cluster 集群模式
  mode: normal
  # cluster 集群模式
  #nodes:
```