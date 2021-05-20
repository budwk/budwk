# wk-gateway

网关中心

## 网关配置说明

```yaml
gateway:
  route:
    load-nacos: true #是否从配置中心同步路由规则
  blacklist:
    enable: false
    ip:
      - 100.100.100.100
      - 127.127.127.127
  # 前缀转发路由
  budwk:
    filter: nacos-prefix #前缀转发路由模式
    serviceNamePrefix: budwk. #前缀名称 如请求 /platform/** 则会转发至 budwk.platform 服务
    priority: 99 #过滤器优先级
  # 重定向路由
  order:
    filter: rewrite-url
    uri: /order/v1/list
    targetUri: /order/v2/list
    serviceName: order
  
nacos:
  discovery:
    server-addr: 192.168.198.19:8848,192.168.198.19:8849,192.168.198.18:8849,192.168.198.18:8848
    naming:
      service-name: gateway
      meta-data: "{'version':'budwk.gateway.7.0.0'}"
  config:
    server-addr: 192.168.198.19:8848,192.168.198.19:8849,192.168.198.18:8849,192.168.198.18:8848
    namespace: public #默认 public
    data-id: route #默认取值 nutz.application.name
    group: DEFAULT_GROUP
    data-type: yaml
  websocket:
    group: DEFAULT_GROUP #nacos服务组,默认 DEFAULT_GROUP
    serviceName: budwk.platform #提供websocket服务的服务名
    url: /platform/websocket #转发路径
```

## 服务方配置说明

```yaml
nacos:
  discovery:
    server-addr: 192.168.198.19:8848,192.168.198.19:8849,192.168.198.18:8849,192.168.198.18:8848
    naming:
      service-name: budwk.platform
      meta-data: "{'version':'budwk.platform.7.0.0'}"
```