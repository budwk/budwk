# wk-starter-web

防SQL注入/防跨站攻击/Cors跨站访问/动作链/表单验证/权限验证 等配置

## 配置说明

```yaml
web:
  xss:
    # 防跨站攻击默认启用,可不配置
    enable: true
  sql:
    # 防SQL注入默认启用,可不配置
    enable: true
    ignore:
      - /api/v1/platform/sys/app/conf/create
      - /api/v1/platform/sys/app/conf/update
  cors:
    # Cors默认启用,可不配置
    enable: true
    # 正式环境请改成部署的域名或网址,增加安全性
    origin: 127.0.0.1
    # 默认 1800 可不配置
    maxage: 1800
    # 请求方法默认 GET,POST,PUT,DELETE 可不配置
    methods: GET,POST,PUT,DELETE
    # 请求头跨域
    headers: X-Requested-With,Content-Type,lang,wk-member-token,wk-user-token
  validation:
    # 表单验证默认false
    enable: true
```

## 表单验证

通过 参数注解```@ApiImplicitParam``` 表单参数注解```@ApiFormParam``` 实体类属性注解 ```@ApiModelProperty ``` 的定义,来验证传参是否符合要求

* 注意: 只验证第一级参数,暂不支持二级以下的对象属性验证
* 验证内容:
1. 验证是否启用 [注解的 validation=true]
2. 验证是否必填 [注解的 required=true]
3. 验证是否匹配正则 [注解的 regex=正则表达式 msg=正则验证失败提示信息]
4. 验证数据类型 [注解的 type=integer|double|float|date 支持几种简单类型,复杂类型请通过正则来验证 ]
* 注意: ```@ApiModelProperty``` param=true 是否为参数为真 并且 validation=true 的时候才进行验证
* 注意: ```@ApiModelProperty``` 没有 type 因为程序可自动获取属性数据类型
* 注意: 除注解定义的数据类型验证之外,适配器在做类型转换时若转换失败也会抛异常,异常会被 ```WkFailProcessor``` 捕获并输出错误