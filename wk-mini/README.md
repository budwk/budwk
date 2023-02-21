# BudWk 单应用版本

## 编译打包

* `mvn package nutzboot:shade`

## 部署运行

* 默认配置 

`nohup java -jar wk-mini.jar >/dev/null 2>&1 &`

* 指定jar中配置 

`nohup java -jar -Dnutz.profiles.active=pro -Xmx450m wk-mini.jar >/dev/null 2>&1 &`

* 加载文件夹中配置 

`nohup java -jar -Dnutz.boot.configure.yaml.dir=/data/budwk/ -Xmx450m wk-mini.jar >/dev/null 2>&1 &`
