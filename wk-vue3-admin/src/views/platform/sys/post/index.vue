<template>
    <div>

</div>
</template>
<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(()=>{
    console.log('post!')
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default{
    name: 'platform-sys-post'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>