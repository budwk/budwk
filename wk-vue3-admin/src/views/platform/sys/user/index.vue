<template>
    <div>
        <el-alert>platformSysUser!</el-alert>
        <el-button @click="getUserInfo('PLATFORM')">ddddd</el-button>
    </div>
</template>
<script setup lang="ts" name="platform-sys-user">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getUserInfo } from '/@/api/ucenter/auth'

const route = useRoute()

onMounted(()=>{
    console.log('user!')
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default{
    name: 'platform-sys-user'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>