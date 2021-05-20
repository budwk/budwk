# wk-starter-database

数据库组件基于 nutz-dao

## 配置说明

```yaml
database:
  enable: true #是否启用数据库功能
  ig:
    snowflake: false #是否启用雪花主键
  global: # 这下面一大堆一般用不着
    checkColumnNameKeyword: false #是否检查字段为数据库的关键字 默认false
    forceWrapColumnName: false #是否把字段名用字符包裹来进行关键字逃逸 默认false
    forceUpperColumnName: false #是否把字段名给变成大写 默认false
    forceHumpColumnName: false #是否把字段名给变成驼峰 默认false
  table:
    create: true #是否自动建表 默认false
    migration: true #是否自动变更 默认false
    add: true # 是否添加列 默认false
    delete: false # 是否删除列 默认false
    check: false # 是否检查索引 默认false
    package: # 相关实体所在包
      - com.budwk
```

## 使用说明

* 启用雪花主键需启用 redis
* Redis雪花算法说明
* 开源版本不提供实现代码

```java
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    // @PrevInsert(els = {@EL("uuid()")})
    @PrevInsert(els = {@EL("snowflake()")})
    private String id;
```