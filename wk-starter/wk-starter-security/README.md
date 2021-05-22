# wk-starter-security

权限验证组件,集成 sa-token

## 配置说明

```yaml
security:
  st:
    # token参数名称,默认 X-Token
    tokenName: X-Token
    # 超时时间(单位s,默认1天)
    timeout: 86400
    # 是否允许同一账号并发登录(为false时新登录挤掉旧登录)
    allowConcurrentLogin: true
    # 在多人登录同一账号时,是否共用一个token(为false时每次登录新建一个token)
    isShare: true
    # Token 风格(默认可取值：uuid,simple-uuid,random-32,random-64,random-128,tik
    tokenStyle: uuid
```