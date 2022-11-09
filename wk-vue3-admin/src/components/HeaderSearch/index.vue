<template>
    <div :class="{ 'show': show }" class="header-search">
      <svg-icon class-name="search-icon" icon-class="search" @click.stop="click" />
      <el-select
        ref="headerSearchSelectRef"
        v-model="search"
        :remote-method="querySearch"
        filterable
        default-first-option
        remote
        placeholder="Search"
        class="header-search-select"
        @change="change"
      >
        <el-option v-for="option in options" :key="option.item.path" :value="option.item" :label="option.item.title.join(' > ')" />
      </el-select>
    </div>
  </template>
  
<script setup lang="ts">
import Fuse from 'fuse.js'
import { getNormalPath, isHttp } from '/@/utils/common'
import { useUserViews } from '/@/stores/userViews'
import { computed, nextTick, onMounted, ref, watch, watchEffect } from 'vue';
import { useRouter } from 'vue-router';
  
const search = ref('');
const options = ref([]);
const searchPool = ref([]);
const show = ref(false);
const fuse = ref(undefined);
const headerSearchSelectRef = ref(null);
const router = useRouter()
const routes = computed(() => useUserViews().routes);
  
function click() {
    show.value = !show.value
    if (show.value) {
        headerSearchSelectRef.value && headerSearchSelectRef.value.focus()
    }
};
function close() {
    headerSearchSelectRef.value && headerSearchSelectRef.value.blur()
    options.value = []
    show.value = false
}
function change(val) {
    const path = val.path;
    if (isHttp(path)) {
        // http(s):// 路径新窗口打开
        const pindex = path.indexOf("http");
        window.open(path.substr(pindex, path.length), "_blank");
    } else {
        router.push(path)
    }
  
    search.value = ''
    options.value = []
    nextTick(() => {
        show.value = false
    })
}
function initFuse(list) {
    fuse.value = new Fuse(list, {
        shouldSort: true,
        threshold: 0.4,
        location: 0,
        distance: 100,
        maxPatternLength: 32,
        minMatchCharLength: 1,
        keys: [{
            name: 'title',
            weight: 0.7
        }, {
            name: 'path',
            weight: 0.3
        }]
    })
}
// Filter out the routes that can be displayed in the sidebar
// And generate the internationalized title
function generateRoutes(routes: any, basePath = '', prefixTitle = []) {
    let res : any = []
  
    for (const r of routes) {
        if (!r.name) { continue }
        const p = r.path.length > 0 && r.path[0] === '/' ? r.path : '/' + r.path;
        const data = {
            path: !isHttp(r.path) ? getNormalPath(basePath + p) : r.path,
            title: [...prefixTitle]
        }
  
        if (r.meta && r.meta.title) {
            data.title = [...data.title, r.meta.title]
            // 有连接的才给搜索
            if (r.redirect !== 'noRedirect' && r.path) {
                res.push(data)
            }
        }
  
        if (r.children) {
            const tempRoutes = generateRoutes(r.children, data.path, data.title)
            if (tempRoutes.length >= 1) {
                res = [...res, ...tempRoutes]
            }
        }
    }
    return res
}
function querySearch(query: string) {
    if (query !== '') {
        options.value = fuse.value.search(query)
    } else {
        options.value = []
    }
}
  
onMounted(() => {
    searchPool.value = generateRoutes(routes.value);
})
  
watchEffect(() => {
    searchPool.value = generateRoutes(routes.value)
})
  
watch(show, (value) => {
    if (value) {
        document.body.addEventListener('click', close)
    } else {
        document.body.removeEventListener('click', close)
    }
})
  
watch(searchPool, (list) => {
    initFuse(list)
})
</script>
  
  <style lang='scss' scoped>
  .header-search {
    font-size: 0 !important;
    cursor: pointer;
    &:hover{
      .svg-icon{
        animation: twinkle 0.3s ease-in-out;
      }
    }
    .search-icon {
      cursor: pointer;
      font-size: 18px;
      vertical-align: middle;
    }
  
    .header-search-select {
      font-size: 18px;
      transition: width 0.2s;
      width: 0;
      overflow: hidden;
      background: transparent;
      border-radius: 0;
      display: inline-block;
      vertical-align: middle;
  
      :deep(.el-input__inner) {
        border-radius: 0;
        border: 0;
        padding-left: 0;
        padding-right: 0;
        box-shadow: none !important;
        border-bottom: 1px solid #d9d9d9;
        vertical-align: middle;
      }
    }
  
    &.show {
      .header-search-select {
        width: 210px;
        margin-left: 10px;
      }
    }
  }
  </style>