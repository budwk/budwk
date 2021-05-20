# wk-starter-sms

短信发送组件

## 配置说明

```yaml
sms:
  enable: true       #是否启用
  #服务提供商
  provider: tencent
  tencent:
    secret-id: 123
    secret-key: 456
    appid: 789
    sign: 大鲨鱼
  template:
    register: 813970  #注册短信验证码模版编号
    login: 813970     #登录短信验证码模版编号
    password: 813970  #找回密码短信验证码模版编号
    message: 813970   #短信通知模版编号
```

## 使用说明

* 使用腾讯云短信则 pom.xml 中加入其SDK
```xml
        <dependency>
            <groupId>com.tencentcloudapi</groupId>
            <artifactId>tencentcloud-sdk-java</artifactId>
        </dependency>
```