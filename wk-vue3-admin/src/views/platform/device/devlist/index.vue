
<template>
    <div class="app-container">
        devlist
    </div>
</template>
<script setup lang="ts" name="platform-device-devlist">
import { onMounted, ref } from "vue"

const goTarget = (url: string) => {
    window.open(url, '__blank')
}

onMounted(() => {
    
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
