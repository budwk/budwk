<template>
    <div v-if="!item.hidden">
      <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren)">
        <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path, onlyOneChild.query)">
          <el-menu-item :index="onlyOneChild.tree" :class="{ 'submenu-title-noDropdown': !isNest }">
            <svg-icon v-if="onlyOneChild.meta&&onlyOneChild.meta.icon" :icon-class="onlyOneChild.meta.icon"/>
            <svg-icon v-else icon-class=""/>
            <template #title><span class="menu-title" :title="hasTitle(onlyOneChild.meta.title)">{{ onlyOneChild.meta.title }}</span></template>
          </el-menu-item>
        </app-link>
      </template>
      
      <el-sub-menu v-else ref="subMenu" :index="item.tree" :class="isTop?'top-nav-submenu':''">
        <template v-if="item.meta" #title>
          <svg-icon v-if="item.meta&&item.meta.icon" :icon-class="item.meta.icon" />
          <span class="menu-title" :title="hasTitle(item.meta.title)">{{ item.meta.title }}</span>
        </template>
        <sidebar-item
          v-for="(child, index) in item.children"
          :key="child.tree + '_'+index"
          :is-nest="true"
          :item="child"
          :is-top="true"
          :class="isTop?'top-nav-submenu':'nest-menu'"
        />
      </el-sub-menu>
    </div>
  </template>
  

<script setup lang="ts">
import AppLink from './Link.vue'
import { ref } from 'vue'
import { isExternal, getNormalPath } from '/@/utils/common' 


const props = defineProps({
    // route object
    item: {
        type: Object,
        required: true
    },
    isNest: {
        type: Boolean,
        default: false
    },
    isTop: {
        type: Boolean,
        default: false
    }
})

const onlyOneChild = ref({});


function hasOneShowingChild(children = [], parent: any) {
    if (!children) {
        children = [];
    }
    const showingChildren = children.filter(item => {
        onlyOneChild.value = item
        return true
    })

    if (showingChildren.length === 1) {
        return true
    }

    if (showingChildren.length === 0) {
        onlyOneChild.value = { ...parent, path: parent.path, noShowingChildren: true }
        return true
    }

    return false
}

function resolvePath(routePath: string, routeQuery: string) {
    if (isExternal(routePath)) {
        return routePath
    }
    if (routeQuery) {
        let query = JSON.parse(routeQuery);
        return { path: routePath, query: query }
    }
    return routePath
}

function hasTitle(title: string){
    if (title.length > 5) {
        return title;
    } else {
        return "";
    }
}
</script>

<style scoped>
.svg-icon {
    margin-right: 5px;
}
</style>