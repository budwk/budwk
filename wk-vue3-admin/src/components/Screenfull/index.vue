<template>
    <div class="screenfull"  @click="toggle">
      <svg-icon :icon-class="isFullscreen ? 'full-screen-cancel' : 'full-screen'"/>
    </div>
  </template>
  
<script setup lang="ts">
import { useFullscreen } from '@vueuse/core'
  
const { isFullscreen, enter, exit, toggle } = useFullscreen()
</script>
  
  <style lang='scss' scoped>
  .screenfull-svg {
    display: inline-block;
    cursor: pointer;
    fill: #5a5e66;
    width: 20px;
    height: 20px;
    vertical-align: 10px;
    &:hover{
      .svg-icon{
        animation: twinkle 0.3s ease-in-out;
      }
    }
  }

  .screenfull {
    cursor: pointer;
    &:hover{
      .svg-icon{
        animation: twinkle 0.3s ease-in-out;
      }
    }
  }

  </style>