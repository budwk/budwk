# wk-starter-config

* 从 Nacos 配置中心获取 yaml 等配置信息
* 从 Nacos 实现服务的发现，gateway 路由等功能
* Nacos 版本 `3.0.2`

## 配置说明

```yaml
nacos:
  discovery:
    server-addr: 10.10.10.3:8848
    namespace: dev
    username: nacos
    password: nacos
    naming:
      service-name: gateway  #网关通过前缀查找服务如服务名: budwk.platform
      meta-data: "{'version':'budwk.gateway.8.0.0'}"
  config:
    server-addr: 10.10.10.3:8848
    namespace: dev #千万别写 public 因为查询条件是命名空间ID,而nacos默认的public命名空间ID是空白!!!
    data-id: wk-gateway
    data-type: yaml
    username: nacos
    password: nacos

```
