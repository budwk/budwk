# wk-starter-common

公共类包

## ResultCode 统一错误编号

| Code | Value | Message |
| ---------|---------|---------| 
| 200 | SUCCESS | 操作成功 |
| 400 | FAILURE | 业务异常 |
| 404 | NOT_FOUND | 服务未找到 |
| 429 | TOO_MANY_REQUESTS | 请求次数过多 |
| 500 | SERVER_ERROR | 服务异常 |
| 500000 | IOC_ERROR | IOC对象加载异常 |
| 500100 | NULL_DATA_ERROR | 数据为空 |
| 500200 | PARAM_ERROR | 参数错误 |
| 500300 | XSS_SQL_ERROR | 传参被拦截 |
| | | | 
| 600101 | USER_NOT_FOUND | 用户不存在 |
| 600102 | USER_DISABLED | 用户被禁用 |
| 600103 | USER_LOCKED | 用户已锁定 |
| 600104 | USER_NAME_ERROR | 用户名错误 |
| 600105 | USER_PWD_ERROR | 用户密码错误 |
| 600106 | USER_PWD_EXPIRED | 用户密码过期 |
| 600107 | USER_VERIFY_ERROR | 验证码错误 |
| 600108 | USER_LOGIN_FAIL | 用户登录失败 |