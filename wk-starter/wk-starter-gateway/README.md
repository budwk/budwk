# wk-starter-gateway

网关组件,基于 nutzcloud-perca 删减改造

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
  # websocket路由(开源版本不提供实现代码)
  websocket:
    group: DEFAULT_GROUP #nacos服务组,默认 DEFAULT_GROUP
    serviceName: budwk.platform #提供websocket服务的服务名
    url: /platform/websocket #转发路径
  
nacos:
  discovery:
    server-addr: 192.168.198.19:8848,192.168.198.19:8849,192.168.198.18:8849,192.168.198.18:8848
    namespace: dev #千万别写 public 因为查询条件是命名空间ID,而nacos默认的public命名空间ID是空白!!!
    naming:
      service-name: gateway
      meta-data: "{'version':'budwk.gateway.7.0.0'}"
  config:
    server-addr: 192.168.198.19:8848,192.168.198.19:8849,192.168.198.18:8849,192.168.198.18:8848
    namespace: dev #千万别写 public 因为查询条件是命名空间ID,而nacos默认的public命名空间ID是空白!!!
    data-id: route #默认取值 nutz.application.name
    data-type: yaml

```

## 服务方配置说明

* 一级路径路由

```yaml
jetty:
  contextPath: /platform
nacos:
  discovery:
    server-addr: 192.168.198.19:8848,192.168.198.19:8849,192.168.198.18:8849,192.168.198.18:8848
    namespace: dev #千万别写 public 因为查询条件是命名空间ID,而nacos默认的public命名空间ID是空白!!!
    naming:
      service-name: budwk.platform
      meta-data: "{'version':'budwk.platform.7.0.0'}"
```

* 二级路径路由

```yaml
jetty:
  contextPath: /demo/admin
nacos:
  discovery:
    server-addr: 192.168.198.19:8848,192.168.198.19:8849,192.168.198.18:8849,192.168.198.18:8848
    namespace: dev #千万别写 public 因为查询条件是命名空间ID,而nacos默认的public命名空间ID是空白!!!
    naming:
      service-name: budwk.demo.admin #对应 /demo/admin
      meta-data: "{'version':'budwk.demo.admin.7.0.0'}"
```