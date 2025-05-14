# wk-starter-dao

* 支持双活切换、多数据源、单数据源模式
* 需将引入的 `nutzboot-starter-nutz-dao` 替换为 `wk-starter-da-dao`
* Nacos 版本 `2.5.1`

## 配置说明

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

jdbc:
  default: A
  mode: da 
  # da=双活模式(支持AB机房数据源同时在线、通过nacos可动态切换,默认数据源为default)
  # many=多数据源模式(支持多数据源同时在线,ADao BDao CDao,命名为英文字母,dao 数据源为 jdbc.url 配置) 
  # ''=单数据源模式,dao 数据源为 jdbc.url 配置
  many:
    A:
      url: jdbc:mysql://127.0.0.1:3306/budwk_a?useUnicode=true&characterEncoding=utf8&useSSL=false
      username: root
      password: "root"
      validationQuery: select 1
      maxActive: 100
      testWhileIdle: true
      connectionProperties: druid.stat.slowSqlMillis=2000
      defaultAutoCommit: true
      slave:
        url: jdbc:mysql://127.0.0.1:3306/budwk_a?useUnicode=true&characterEncoding=utf8&useSSL=false
        username: root
        password: "root"
        validationQuery: select 1
        maxActive: 100
        testWhileIdle: true
        connectionProperties: druid.stat.slowSqlMillis=2000
        defaultAutoCommit: true
    B:
      url: jdbc:mysql://127.0.0.1:3306/budwk_b?useUnicode=true&characterEncoding=utf8&useSSL=false
      username: root
      password: "root"
      validationQuery: select 1
      maxActive: 100
      testWhileIdle: true
      connectionProperties: druid.stat.slowSqlMillis=2000
      defaultAutoCommit: true
      slave:
        url: jdbc:mysql://127.0.0.1:3306/budwk_b?useUnicode=true&characterEncoding=utf8&useSSL=false
        username: root
        password: "root"
        validationQuery: select 1
        maxActive: 100
        testWhileIdle: true
        connectionProperties: druid.stat.slowSqlMillis=2000
        defaultAutoCommit: true
```
