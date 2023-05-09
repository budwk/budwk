# wk-device-handler 协议解析器

## 开发步骤

* `cd wk-device/wk-device-handler`
* `mvn clean package install`
* 编译安装 `wk-device-handler` 后开始开发协议解析器

|模块名称|功能描述|
----|------
|wk-device-handler-demo-meter|模拟表具通信，演示TCP/UDP 和 AEP HTTP的解析过程|
|wk-device-handler-demo-tcp|模拟TCP通信，演示TCP温度计解析过程|
|wk-device-handler-xxx|其他设备协议解析器|
