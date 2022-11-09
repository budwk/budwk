# wk-starter-apiauth

API接口签名认证

# 使用方法

在需要进行签名校验的类上加上注解
```java
@Filters(@By(type = ApiSignFilter.class))
public class TestSignController {

}
```

如果获取到了 `IApiAuthProvider` 的实例，那么则通过`IApiAuthProvider`来获取`appKey`进行校验。否则会获取配置文件中的配置。

配置格式如下

```yaml
api:
  auth:
    sign:
      appid值: appkey值
```