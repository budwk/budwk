# wk-device-storage 设备数据存储

* 引入依赖

```xml
<dependency>
    <groupId>com.budwk.app</groupId>
    <artifactId>wk-device-storage-starter</artifactId>
    <version>${project.version}</version>
</dependency>
<dependency>
    <groupId>com.budwk.app</groupId>
    <artifactId>wk-device-storage-mongodb</artifactId>
    <version>${project.version}</version>
</dependency>
```

* 设备数据存储配置

```yaml
device:
  storage:
    # 原始报文存储时间(单位天,默认30天  仅对mongodb有效) 
    raw-save-ttl: 30
    # 解析后数据存储时间(单位天,默认0=永久存储  仅对mongodb有效,tdengine 是建库语句设置)
    data-save-ttl: 0
    # 解析后数据存储方式(mongodb/tdengine)
    data-save-type: mongodb
```

* 使用 MongoDB 数据库时配置内容

```yaml
mongodb:
  # mongodb://用户名:密码@IP:端口/数据库名
  url: mongodb://127.0.0.1:27017/device_test
```

* 使用 TDEngine 数据库时配置内容

```yaml
tdengine:
  enable: true
  jdbc:
    type: druid
    driverClassName: com.taosdata.jdbc.TSDBDriver
    url: jdbc:TAOS://127.0.0.1:6030/device_test?timezone=UTC-8
    # Restful API
    # driverClassName: com.taosdata.jdbc.rs.RestfulDriver
    # url: jdbc:TAOS-RS://127.0.0.1:6041/device_test?timezone=UTC-8
    username: root
    password: taosdata
    maxActive: 10
    maxIdle: 10
    minIdle: 1
    testWhileIdle: true
    validationQuery: select server_status()
```