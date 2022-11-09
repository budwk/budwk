
<template>
    <div class="reload-page"  @click="reloadPage">
      <svg-icon icon-class="reload"/>
    </div>
  </template>
  
<script setup lang="ts">
import { useTagsView } from '/@/stores/tagsView'
import router from "/@/router"

const reloadPage = () => {
    const { path, query } = router.currentRoute.value
    const name = path.substring(1).replaceAll('/','-')
    const obj = {name:name, path: path, query: query}
    return useTagsView().delCachedView(obj).then(() => {
        const { path, query } = obj
        router.replace({
            path: '/redirect' + path,
            query: query
        })
    })
}
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

  .reload-page {
    cursor: pointer;
    fill: #5a5e66;
    &:hover{
      .svg-icon{
        animation: twinkle 0.3s ease-in-out;
      }
    }
  }

  </style>