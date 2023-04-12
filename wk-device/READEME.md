# wk-device 设备中心

通用、简洁、性能可靠、易扩展的物联网设备接入和管理平台


模块名称 | 模块功能 | 部署运行 | 说明 |
----|------|----|----
wk-device-protocol | 设备协议解析 | | 独立项目以便协议开发者只用关心协议本身 
&nbsp;&nbsp;&nbsp;wk-device-protocol-core | 协议解析核心包  | |
&nbsp;&nbsp;&nbsp;wk-device-protocol-demo | demo设备协议  | |
wk-device-network | 网络协议支持  ||
&nbsp;&nbsp;&nbsp;wk-device-network-tcp | TCP协议  ||
&nbsp;&nbsp;&nbsp;wk-device-network-udp | UDP协议  ||
&nbsp;&nbsp;&nbsp;wk-device-network-http | HTTP协议  ||
wk-device-gateway | 设备通信网关 | 是 | 加载 network 协议模块提供协议接入服务
wk-device-hanlder | 协议解析加载  | 是 | 加载 protocol 设备协议解析包
wk-device-processor | 业务处理  | 是 | 业务处理、规则引擎、支持拓展计费等自定义业务
wk-device-cache | 设备缓存  | | 产品信息缓存、设备基本信息缓存
wk-device-storage | 数据存储  | | 原始报文存储、上报数据存储、事件数据存储
wk-device-common | 公共类、接口类  | | Pojo、Enum、Util
wk-device-server | 业务类、控制类 | 是 | 提供网页管理API、业务代码实现

## 运行依赖

名称 | 功能 | 是否必须 | 版本要求 | 说明 |
----|------|----|----|----
Redis | 缓存 | 是 | 无 |缓存服务提升读取性能 |
MariaDB 或 MySQL | 数据库 | 是 | MariaDB 10+ 或 MySQL 5.8+ |基本信息数据、业务结果数据、统计数据等 |
Kafka | 消息队列 | 是 | Kafka 3.4.0 | 支撑系统内部的数据流转 |
MongoDB | NoSQL数据库 | 是 | MongoDB 6.0 |原始报文、操作日志等过期自动删除数据 |
MongoDB 或 TDengine | 时序数据库 | 是 | MongoDB 6.0 或 TDengine 3.0 | 设备有效上报数据 |
