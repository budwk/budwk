# Starter组件

## 组件说明

| 组件名称 | 组件说明 | 作用域 | 使用说明 | 依赖关系 | 
| ---------|---------| ----------| ----------| ----------| 
| wk-starter-dependencies | 依赖pom | 所有NB项目 | 依赖关系及版本号 |
| wk-starter-common | 公共类组件 | 所有NB项目 | Result/Page/全局变量/工具类等,详见 [README](wk-starter-common/README.md)|
| wk-starter-database | 数据库组件 | 服务层 | 数据库操作公共方法,详见 [README](wk-starter-database/README.md) | nutzboot-starter-nutz-dao, nutzboot-starter-redis(若启用雪花算法) |
| wk-starter-openapi | 接口文档生成组件 | 控制层 | 通过注解定义生成API文档,详见 [README](wk-starter-openapi/README.md)| |
| wk-starter-security | 权限验证组件 | 控制层 | 详见 [README](wk-starter-security/README.md) | nutzboot-starter-redis, nutzboot-starter-nutz-mvc |
| wk-starter-web | Web组件 | 控制层 | 防止跨站攻击/SQL注入/CORS/表单验证/权限验证,详见 [README](wk-starter-web/README.md) | nutzboot-starter-nutz-mvc |
| wk-starter-websocket | WebSocket组件 | 控制层 | 详见 [README](wk-starter-websocket/README.md)| nutzboot-starter-nutz-mvc |
| wk-starter-log | 日志组件 | 控制层 |  详见 [README](wk-starter-log/README.md) | wk-starter-security (nutzboot-starter-redis), nutzboot-starter-dubbo (使用 @SLog 注解需 wk-platform 启动) |
| wk-starter-gateway | 网关组件 | 控制层 | 详见 [README](wk-starter-gateway/README.md) | nutzboot-starter-nacos-discovery, nutzboot-starter-nacos-config |
| wk-starter-dubbo | RPC组件 | 服务层 | 详见 [README](wk-starter-dubbo/README.md) | |
| wk-starter-sms | 短信组件 | 服务层 | 详见 [README](wk-starter-sms/README.md) | 短信服务商SDK |
| wk-starter-email | 邮件组件 | 服务层 | 详见 [README](wk-starter-email/README.md) |  |
| wk-starter-storage | 文件服务组件 | 服务层 | 详见 [README](wk-starter-storage/README.md) |  |
| wk-starter-apiauth | 对外API接口验证 | 控制层 | 详见 [README](wk-starter-apiauth/README.md) |  |

## 开发事项

* Redis 
  
在网关中心、权限中心、方法缓存等处有较深的应用，在适当业务处使用有提供性能的好处，但不宜所有地方都加方法缓存，且记得缓存一定要加上失效时间

* 主键生成

在分布式系统里，禁止使用数据库自带的自增长类型生成主键，本框架可配置使用 uuid 或 redis 来生成雪花主键，在性能有要求或生成有序可见的主键时推荐使用 redis (SnowFlake雪花算法)，更高性能要求的场景如物联网设备可尝试使用时序数据库存储数据

* 技术选型

不同的技术框架解决不同的问题，在特定业务场景，选择合适的技术实现方式至关重要，如 NoSQL、搜索引擎、MQ 等适用场景，从性能和长远角度考虑清楚



