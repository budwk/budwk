# 简介

`Vue3` + `Vite4` + `Element-Plus` + `TypeScript` + `Pinia`

# 编译

## dev
`npm install pnpm`

`pnpm run dev`

## build
`pnpm run build`

# 开发

## 权限验证

    * 系统登录后从后台获取用户权限标识
    * 前端采用 `v-permission="['sys.manage.unit.create']"` 进行验证

## 图标使用

### el图标
    * 使用方式1: `<icon name="el-icon-Right" size="size" color="color" />`
    * 使用方式2: `<right style="width: 1em; height: 1em;" />`

### svg图标    
    * 使用方式: `<svg-icon icon-class="reload" />`

## 页面名称

* 组件名称严格按照文件夹名称命名,多级文件夹用-分割
* 用途1: 标签页的缓存功能
* 用途2: 标签页页面刷新功能
* 用途3: 系统自动生成路由
```
<script lang="ts" name="platform-sys-unit">
// name组件名
</script>
```

## 页面布局

```
<route lang="yaml">
    meta:
      layout: platform/index
</route>
```