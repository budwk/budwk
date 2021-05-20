# wk-starter-email

邮件发送组件

## 配置说明

```yaml
email:
  enable: true                  #是否启用
  HostName: smtp.exmail.qq.com  #服务器域名或IP
  SmtpPort: 465                 #服务器端口
  UserName: wizzer@qq.com       #账号
  Password: 123456              #密码
  SSLOnConnect: true            #开启SSL连接
  From: wizzer@qq.com           #发件人
  charset: UTF-8                #字符集, 默认UTF-8
  template:
    domain: https://budwk.com   #邮件内容中留下的域名
    author: 大鲨鱼               #邮件内容中留下的落款
```
