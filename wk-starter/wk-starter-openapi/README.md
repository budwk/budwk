# wk-starter-openapi

通过配置及注解的定义,生成 Swagger(OpenAPI V3) 格式的接口文档

## 配置说明

```yaml
openapi:
  # 是否启用
  enable: true
  scanner:
    # 扫描包路径
    package: com.budwk.test
  info:
    # 文档标题
    title: BudWk V8 API
    # 文档版本
    version: 1.0.0
    # 作者信息
    contact:
      name: 大鲨鱼
      email: wizzer@qq.com
  servers:
    # 服务器地址,默认为空则为本机服务
    - url: ""
      description: 本机环境
    - url: https://demo.budwk.com/
      description: 演示环境
    # 头域传递都token参数名称
  headers:
    - value: X-Token
      name: 用户Token
      in: header
      required: true
```

## 注解说明

* [权限注解] 复用 sa-token 的注解,来生成接口权限说明

```text
@RequiresAuthentication 要求登陆
@RequiresPermissions    要求分配权限标识
@RequiresRoles          要求分配角色
```

* [类的注解] 定义的注解可以供表单验证组件使用

```text
@ApiModel           模型类
@ApiModelProperty   属性说明
```
ApiModelProperty

| 名称 | 说明 | 作用范围 |
| ---------|---------|---------| 
| name | 参数名称 | 共用 |
| description | 参数说明 | 共用 |
| required | 是否必填 | 共用 |
| example | 示例 | OpenAPI | 
| df | 默认值 | OpenAPI | 
| param | 是否表单参数 | OpenAPI | 
| check | 是否启用表单验证 | 表单验证  | 
| validation | 正则枚举 | 表单验证  | 
| regex | 表单验证正则规则 | 表单验证 | 
| msg | 表单验证失败提示 | 表单验证 |

```text
@ApiDefinition      需要解析的控制类
@ApiOperation       需要解析的控制类方法
```
ApiOperation

| 名称 | 说明 |  
| ---------|---------| 
| name | 方法名称 |  
| description | 方法说明 |


* [传参注解] 定义 path/query/header/cookie 的传参

```text
@ApiImplicitParams      参数组
@ApiImplicitParam       参数定义
```
ApiImplicitParam

| 名称 | 说明 | 作用范围 |
| ---------|---------|---------| 
| name | 参数名称 | 共用 |
| description | 参数说明 | 共用 |
| required | 是否必填 | 共用 |
| type | 参数类型 | OpenAPI |
| example | 示例 | OpenAPI | 
| df | 默认值 | OpenAPI | 
| in | 参数位置 | 共用 | 
| check | 是否启用表单验证 | 表单验证  | 
| validation | 正则枚举 | 表单验证  | 
| regex | 表单验证正则规则 | 表单验证 | 
| msg | 表单验证失败提示 | 表单验证 |

* [表单注解] 定义的注解可以供表单验证组件使用
```text
@ApiFormParams      表单参数组
@ApiFormParam       表单参数定义
```

ApiFormParams

| 名称 | 说明 |
| ---------|---------|
| value | ApiFormParam 数组 |
| implementation | 带 @ApiModel 注解的实体类 |
| mediaType | mediaType 类型 | 

ApiFormParam

| 名称 | 说明 | 作用范围 |
| ---------|---------|---------| 
| name | 参数名称 | 共用 |
| description | 参数说明 | 共用 |
| required | 是否必填 | 共用 |
| type | 参数类型 | OpenAPI |
| example | 示例 | OpenAPI | 
| df | 默认值 | OpenAPI | 
| check | 是否启用表单验证 | 表单验证  | 
| validation | 正则枚举 | 表单验证  | 
| regex | 表单验证正则规则 | 表单验证 | 
| msg | 表单验证失败提示 | 表单验证 |

* [响应注解] 定义的注解可以供表单验证组件使用

```text
@ApiResponses      响应字段组
@ApiResponse       响应字段定义
```

ApiResponses

| 名称 | 说明 |
| ---------|---------|
| value | ApiResponse 数组 |
| implementation | 带 @ApiModel 注解的实体类 |
| mediaType | mediaType 类型 | 
| example | 示例,完整的Json格式字符串 | 

ApiResponse

| 名称 | 说明 | 
| ---------|---------|
| name | 参数名称 | 
| type | 参数类型 | 
| description | 参数说明 |