# wk-starter-security

权限验证组件,集成 sa-token

## 配置说明

```yaml
security:
  st:
    # token参数名称,默认 wk-user-token
    tokenName: wk-user-token
    # 是否尝试从cookie里读取token
    isReadCookie: false
    # token临时有效期 [指定时间内无操作就视为token过期] (单位: 秒), 默认-1 代表不限制 (例如可以设置为1800代表30分钟内无操作就过期)
    activityTimeout: 86400
    # token的长久有效期(单位:秒) 默认30天, -1代表永久
    timeout: 86400
    # 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
    isConcurrent: true
    # 在多人登录同一账号时,是否共用一个token(为false时每次登录新建一个token)
    isShare: true
    # Token 风格(默认可取值：uuid,simple-uuid,random-32,random-64,random-128,tik
    tokenStyle: uuid
```