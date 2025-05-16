# wk-starter-redisjson

* 使用 RedisJsonService 需安装 redisjson 插件

redis 连接配置和之前一样

## 非双活配置

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

## 双活配置

```yaml

da:
  nacos: #通过nacos切换数据源 {"datasource": "A"}
    enable: true
    server-addr: 10.10.10.3:8848
    namespace: da #千万别写 public 因为查询条件是命名空间ID,而nacos默认的public命名空间ID是空白!!!
    data-id: active
    data-type: json #仅支持json
    group: DEFAULT_GROUP
    username: nacos
    password: nacos
    
redis:
  default: A
  mode: da
  many:
      A:
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
      B:
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