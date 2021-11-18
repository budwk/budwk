<div align="center">
    <br/>
    <h1>BudWk 开源企业级Java Web开发框架</h1>

[![Gitee GVP](https://gitee.com/wizzer/NutzWk/badge/star.svg?theme=gvp)](https://gitee.com/wizzer/NutzWk)
[![GitHub release](https://img.shields.io/github/release/budwk/budwk-nutzboot.svg)](https://github.com/budwk/budwk/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![PowerByNutz](https://img.shields.io/badge/PowerBy-Nutz-green.svg)](https://github.com/nutzam/nutz)
</div>

<div align="center">

**[官网](https://budwk.com)** |
**[V7演示地址](https://demo.budwk.com)** |
**[V7开发指南](https://budwk.com/docs)** |
**[V5演示地址](https://nutzwk.wizzer.cn)** |
**[捐赠清单](https://budwk.com/donation)** |

</div>

> 在力所能及的情况下，最大限度的提高Web开发人员的生产力

# 🚀 简介

BudWk V7 进行了大量重构，与之前版本结构大不相同，增加网关中心、认证中心、控制中心等，并且完全组件化、配置化，大大减轻开发工作量，提升开发效率的同时为产品升级迭代提供了极大便利。

BudWk 原名 NutzWk ，是基于国产框架 nutz 及 nutzboot 开发的开源Web基础项目，集权限体系、系统参数、数据字典、站内消息、定时任务、CMS、微信等最常用功能，不庞杂、不面面俱到，使其具有上手容易、开发便捷、扩展灵活等特性，特别适合各类大中小型定制化项目需求。

自2012年开源至今，以“在力所能及的情况下，最大限度的提高Web开发人员的生产力”为宗旨，紧跟时代技术发展，发布V1-V7多个版本，也尝试在开源和持续发展的道路上求索。本次V7将发布 社区开源版、商业基础版、企业高级版，社区开源版包含完整的前后端源码，采用MIT开源协议及《计算机软件著作权》版权保护。

### QQ交流群

*  1群: 24457628
*  2群: 68428921

# 🎉 本版说明(BudWk v7.x)

## 运行环境

*   JDK 11 + 或 OpenJDK 11 +
*   Redis 4.0.8 +
*   MariaDB 10.x + 或 MySql 5.7、Oracle、SqlServer、达梦等
*   Nacos 2.0.3 +

## 开发工具
*   IntelliJ IDEA
*   Visual Studio Code
*   Node 12.13.0 +
*   Maven 3.6.3 +
*   Git

## 架构图

![BUDWK架构](main.png)

## 目录结构

```lua
budwk                               -- 根目录
│  ├─wk-starter                     -- 组件中心
│  │  ├─wk-starter-common           -- 通用类组件
│  │  ├─wk-starter-database         -- 数据库组件
│  │  ├─wk-starter-config           -- 配置组件(商业版)
│  │  ├─wk-starter-dependencies     -- 所有依赖
│  │  ├─wk-starter-dubbo            -- Dubbo组件
│  │  ├─wk-starter-email            -- Email组件
│  │  ├─wk-starter-gateway          -- 网关组件
│  │  ├─wk-starter-job              -- 简易定时任务组件
│  │  ├─wk-starter-log              -- 日志及SLog组件
│  │  ├─wk-starter-openapi          -- 接口文档生成组件
│  │  ├─wk-starter-security         -- 权限验证组件
│  │  ├─wk-starter-sms              -- 短信发送组件
│  │  ├─wk-starter-storage          -- 文件存储组件
│  │  ├─wk-starter-web              -- WEB拦截跨越表单验证组件
│  ├─wk-gateway                     -- 网关中心
│  │  ├─websocket                   -- WebScoket支持(商业版)
│  ├─wk-platform                    -- 控制中心
│  │  ├─wk-platform-common          -- 通用类供其他模块调用
│  │  ├─wk-platform-server          -- 服务类提供API及RPC服务
│  ├─wk-ucenter                     -- 认证中心
│  ├─wk-cms                         -- CMS管理
│  │  ├─wk-cms-common               -- 通用类供其他模块调用
│  │  ├─wk-cms-server               -- 服务类提供API及RPC服务
│  ├─wk-wechat                      -- 微信管理(商业版)
│  │  ├─wk-wechat-common            -- 通用类供其他模块调用(商业版)
│  │  ├─wk-wechat-server            -- 服务类提供API及RPC服务(商业版)
│  ├─wk-vue-admin                   -- Vue前端代码
│  │  ├─pages-home                  -- 消息中心
│  │  ├─pages-platform              -- 控制中心
│  │  ├─pages-cms                   -- CMS管理
│  │  ├─pages-wechat                -- 微信管理(商业版)
```
* 代码生成器IDEA插件,请下载安装 [https://gitee.com/budwk/budwk-codegenerator](https://gitee.com/budwk/budwk-codegenerator)
* 推荐组件中心、控制中心、前端等功能模块独立创建Git仓库,便于权限管理及升级迭代

## 技术选型
### 后端技术
技术 | 名称 | 官网
----|------|----
Nutz | JavaEE应用框架  | [https://nutzam.com](https://nutzam.com)
NutzBoot | 微服务框架  | [https://github.com/nutzam/nutzboot](https://github.com/nutzam/nutzboot)
SaToken | 权限框架  | [http://sa-token.dev33.cn](http://sa-token.dev33.cn)
Druid | 数据库连接池  | [https://github.com/alibaba/druid](https://github.com/alibaba/druid)
Nacos | 配置及注册中心  | [https://nacos.io](https://nacos.io)
Dubbo | 分布式服务框架  | [https://dubbo.apache.org](https://dubbo.apache.org)
Redis | 分布式缓存数据库  | [https://redis.io](https://redis.io)
Quartz | 作业调度框架  | [https://www.quartz-scheduler.org](https://www.quartz-scheduler.org)
IdGenerator | 雪花主键生成  | [https://github.com/yitter/IdGenerator](https://github.com/yitter/IdGenerator)
Hutool | 工具集合  | [https://hutool.cn](https://hutool.cn)

### 前端技术
技术 | 名称 | 官网
----|------|----
Vue.js | MVVM框架 | [https://vuejs.org](https://vuejs.org)
Nuxt.js | Vue通用应用框架 | [https://nuxtjs.org](https://nuxtjs.org)
Element | 基于Vue的UI框架 | [https://element.eleme.io](https://element.eleme.io)
Font-awesome | 字体图标  | [https://fontawesome.com](https://fontawesome.com)

## 开发指南
*   确保 MySql、Redis、Nacos 默认端口配置并已启动
*   MySql 创建名为 `budwk_v7` 的空数据库,在每个微服务模块启动时会自动建表,同时初始化数据
*   在单个NB模块下执行 `mvn compile nutzboot:run` 运行或 `mvn package nutzboot:shade` 生成可执行jar包
*   在后端项目根目录执行 `mvn -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -Dnutzboot.dst=E:/dst clean package nutzboot:shade` 可将所有可运行jar包生成到指定位置
*   分别启动jar文件 `nohup java -jar budwk.jar >/dev/null 2>&1 &`
*   正常启动前端后访问 `http://127.0.0.1:8800` 用户名 superadmin 密码 1
*   API调试 `http://127.0.0.1:9900/platform/openapi` `http://127.0.0.1:9900/ucenter/openapi` 等

## 服务器部署

*   指定配置文件运行 `nohup java -jar -Dnutz.profiles.active=pro -Xmx450m wk-platform-server.jar >/dev/null 2>&1 &`

# 🤝 鸣谢

*   [@wendal](https://github.com/wendal) 代码贡献者,Nutz/LuatOS主要作者
*   [@rekoe](https://github.com/Rekoe) 代码贡献者
*   [@enilu](https://github.com/enilu) IDEA插件代码贡献者
*   [@threefish](https://github.com/threefish) IDEA插件代码贡献者
*   [@loyalove](https://github.com/loyalove) 前端代码贡献者
*   [@syrxw](https://github.com/syrxw) 前端代码贡献者

<br/>

> 如果您觉得还不错请在右上角点一下 star，帮忙转发，谢谢 🙏🙏🙏 大家的支持是开源最大动力
