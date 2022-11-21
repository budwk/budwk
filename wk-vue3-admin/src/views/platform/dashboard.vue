
<template>
    <div>
        Dashboard!
        <el-row v-role="['sysadmin']">
        
        </el-row>
    </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue"

const a = ref('')
onMounted(() =>{
    //console.log('Dashboard onMounted!!!')
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default{
    name: 'platform-dashboard'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>