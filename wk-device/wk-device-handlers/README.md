# wk-device-handler 协议解析器

## 开发步骤

* `cd wk-starter` 执行 `mvn install` 编译安装组件库
* `cd wk-device` 执行 `mvn install` 编译安装设备模块

|模块名称|功能描述|
----|------
|wk-device-handler-demo-meter|模拟表具通信，演示TCP/UDP 和 AEP HTTP的解析过程|
|wk-device-handler-demo-tcp|模拟TCP通信，演示TCP温度计解析过程|
|wk-device-handler-xxx|其他设备协议解析器|
