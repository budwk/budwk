# wk-starter-tdengine

涛思时序数据库

## 配置说明

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
