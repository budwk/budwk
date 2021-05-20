# wk-starter-log

日志组件

## 配置说明

```yaml
log:
  # database=存数据库,,mongodb=mongodb 此配置项只需要在 wk-platform 配置即可
  save: mongodb
  # root 日志等级,默认 info
  level: debug
  # 是否控制台彩色显示,默认 false
  color: false
  # 日志存储根文件夹,无需设置项目名称,程序会自动拼接路径,默认 logs
  path: "logs"
  

```

## 使用说明

* SLog 的日志存储是通过 dubbo 远程调用 wk-platform-server 实现的,所以需启用 dubbo + wk-platform-server
* `tag` 属性仅需要在类上声明一次,不用在方法上添加
* `value` 支持el表达式,如获取变量 `${task.name}`