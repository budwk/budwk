# WK Vue3 Admin - Copilot Instructions

## 项目概述
这是 BudWk 平台的 Vue3 管理后台项目，基于 Vue3 + Vite + Element-Plus + TypeScript + Pinia 构建。

## 技术栈与架构

### 核心依赖
- **Vue 3.4+** + Composition API
- **Element Plus 2.13+** 作为 UI 组件库
- **Pinia** 状态管理，带持久化 (`pinia-plugin-persistedstate`)
- **vue-i18n** 国际化支持 (中/英)
- **axios** HTTP 请求

### 路由系统 (关键架构)
使用 `vite-plugin-pages` 自动生成路由，基于 `src/views/` 目录结构：
- 文件路径即路由路径，如 `src/views/platform/sys/user/index.vue` → `/platform/sys/user`
- 支持嵌套路由和动态路由参数
- 页面布局通过路由元信息配置，统一管理：
```
/plugins/pages.ts
```

## 关键约定

### 组件命名规范
组件 name 必须严格按照文件夹路径命名，用 `-` 分割，这对标签页缓存和刷新功能至关重要：
```vue
<script lang="ts" name="platform-sys-user">
// 对应路径: src/views/platform/sys/user/
</script>
```

### 权限控制
使用自定义指令进行权限验证，权限标识从后端获取存储在 `userInfo` store：
```vue
<!-- 权限验证 -->
<el-button v-permission="['sys.manage.user.create']">新增</el-button>

<!-- 角色验证 -->
<el-button v-role="['sysadmin','admin']">管理</el-button>
```
指令实现: [src/directive/directives.ts](src/directive/directives.ts)

### API 请求模式
所有 API 统一封装在 `src/api/` 目录下，按模块分类：
```typescript
// src/api/platform/sys/user.ts
export const API_SYS_USER_CREATE = '/platform/sys/user/create'

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_USER_CREATE,
        method: 'POST',
        data: data
    })
}
```

请求工具自动处理：Token 注入 (`wk-user-token`)、错误提示、登录状态检测。

### 图标使用
```vue
<!-- Element Plus 图标 -->
<icon name="el-icon-Right" size="size" color="color" />

<!-- 自定义 SVG 图标 (存放在 src/assets/icons/svg/) -->
<svg-icon icon-class="reload" />
```

### Store 模式
使用 Pinia with 持久化，关键 stores：
- `userInfo` - 用户信息、权限、菜单
- `userViews` - 动态路由视图
- `userSettings` - 主题配置
- `tagsView` - 标签页状态

## 开发命令
```bash
pnpm install          # 安装依赖 (需 Node.js v22)
pnpm run dev          # 启动开发服务器
pnpm run build        # 构建生产版本
pnpm run lint-fix     # 修复 ESLint 问题
```

## 项目结构要点
- `src/views/platform/` - 平台管理页面 (自动生成路由)
- `src/components/` - 通用组件 (ProTableList, CronTab, Import/Export 等)
- `src/layouts/` - 布局组件
- `src/stores/` - Pinia stores
- `src/api/` - API 接口定义
- `src/utils/request.ts` - Axios 封装
