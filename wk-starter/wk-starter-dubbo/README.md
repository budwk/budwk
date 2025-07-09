# wk-starter-dubbo

Dubbo组件

## 配置参数详解

### 基础配置

| 参数 | 说明 | 默认值 | 示例                                             |
|-----|------|--------|------------------------------------------------|
| `dubbo.enabled` | 是否启用Dubbo | `true` | `true`                                         |
| `dubbo.application.name` | 应用名称 | `nutzboot-dubbo-app` | `my-app`                                       |
| `dubbo.scan.packages` | 扫描包路径（多个包用逗号分隔） | `` | 默认不用陪着自动获取 AppContext 包名 `com.example.service` |

### 协议配置

| 参数 | 说明 | 默认值 | 示例 |
|-----|------|--------|------|
| `dubbo.protocol.name` | 协议名称 | `dubbo` | `dubbo`, `triple` |
| `dubbo.protocol.port` | 协议端口 | `20880` | `20880` |

### 注册中心配置

| 参数 | 说明 | 默认值 | 示例 |
|-----|------|--------|------|
| `dubbo.registry.address` | 注册中心地址 | `` | `nacos://10.10.10.3:8848?namespace=dev&username=nacos&password=nacos&contextPath=/nacos` |

### 消费者配置

| 参数 | 说明 | 默认值 | 示例 |
|-----|------|--------|------|
| `dubbo.consumer.timeout` | 调用超时时间(毫秒) | `3000` | `5000` |
| `dubbo.consumer.retries` | 重试次数 | `0` | `2` |
| `dubbo.consumer.loadbalance` | 负载均衡策略 | `random` | `random`, `roundrobin`, `leastactive` |
| `dubbo.consumer.cluster` | 集群容错策略 | `failover` | `failover`, `failfast`, `failsafe` |
| `dubbo.consumer.check` | 是否检查提供者存在 | `true` | `false` |

### 提供者配置

| 参数 | 说明 | 默认值 | 示例 |
|-----|------|--------|------|
| `dubbo.provider.timeout` | 服务超时时间(毫秒) | `3000` | `5000` |
| `dubbo.provider.delay` | 延迟暴露时间(毫秒) | `-1` | `5000` |

### 高级配置

| 参数 | 说明 | 默认值 | 示例 |
|-----|------|--------|------|
| `dubbo.monitor.address` | 监控中心地址 | `` | `dubbo://127.0.0.1:7070/MonitorService` |
| `dubbo.service.group` | 默认服务分组 | `` | `default` |
| `dubbo.service.version` | 默认服务版本 | `` | `1.0.0` |

