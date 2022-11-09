<template>
    <el-breadcrumb class="app-breadcrumb" separator="/">
      <transition-group name="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/platform/dashboard' }" :key="'dashboard'">控制台</el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item) in levelList" :key="item">
        <span class="no-redirect">{{ item }}</span>
      </el-breadcrumb-item>
      </transition-group>
    </el-breadcrumb>
  </template>
  
<script setup lang="ts">
import { ref, watchEffect } from "vue"
import { useRouter } from "vue-router"
import { useRoute } from "vue-router"
const route = useRoute()
const router = useRouter()
const levelList = ref([])
  
function getBreadcrumb() {
    if(isDashboard(route)){
        return
    }
    if(route.meta && route.meta.breadcrumb && route.meta.breadcrumb.indexOf('|') > 0) {
        levelList.value = route.meta.breadcrumb.split('|')
    } else if(route.meta && route.meta.breadcrumb) {
        levelList.value.push(route.meta.breadcrumb)
    }
}
function isDashboard(route: any) {
    const name = route && route.name
    if (!name) {
        return false
    }
    return name.trim() === 'platformDashboard'
}
function handleLink(item: any) {
    const { redirect, path } = item
    if (redirect) {
        router.push(redirect)
        return
    }
    router.push(path)
}
  
watchEffect(() => {
    if (route.path.startsWith('/redirect/')) {
        return
    }
    getBreadcrumb()
})
getBreadcrumb();
</script>
  
  <style lang='scss' scoped>
  .app-breadcrumb.el-breadcrumb {
    display: inline-block;
    font-size: 14px;
    line-height: 50px;
    margin-left: 8px;
  
    .no-redirect {
      color: #97a8be;
      cursor: text;
    }
  }
  </style>