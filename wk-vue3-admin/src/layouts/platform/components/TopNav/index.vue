<template>
    <div class="top-nav-menus">
        <el-menu
            :default-active="activeMenu" :unique-opened="true"
            :active-text-color="themeColor" :collapse-transition="false" mode="horizontal">
            <SidebarItem v-for="(route, index) in sidebarRouters" :key="route.tree + '_' + index" :item="route" :is-top="true"/>
        </el-menu>
    </div>
</template>
  
<script setup lang="ts">
import { isExternal, isHttp } from '/@/utils/common'
import { useUserSettings } from '/@/stores/userSettings'
import { useUserViews } from '/@/stores/userViews'
import { useClient } from '/@/stores/client'
import { computed, onBeforeUnmount, onMounted, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import variables from '/@/assets/styles/variables.module.scss'
import SidebarItem from '../Sidebar/SidebarItem.vue'

const visibleWidth = ref('300px');

const sidebarRouters = computed(() => useUserViews().sidebarRouters)
const showLogo = computed(() => true)
const sideTheme = computed(() => useUserSettings().sideTheme)
const themeColor = computed(() => useUserSettings().themeColor)
const topNav = computed(() => useUserSettings().topNav)
const isCollapse = computed(() => !useClient().sidebar.opened)

const setVisibleWdith = () =>{
    const width : number = document.body.getBoundingClientRect().width
    visibleWidth.value = parseInt(width/2)+'px'
}
// 默认激活的菜单
const activeMenu = computed(() => {
    const { meta, path } = useRoute()
    if (meta.activeMenu) {
        return meta.activeMenu
    }
    if (meta.tree) {
        return meta.tree
    }
    return '0'
})

onMounted(() => {
    setVisibleWdith()
})
</script>

<style lang="scss">
.top-nav-menus {
    min-width: v-bind(visibleWidth);
}
.top-nav-submenu {
    text-align: 'left';
    &>.el-sub-menu__title {
          padding-right: 20px !important;
          .svg-icon {
            margin-left: 5px !important;
          }
          &>span {
            margin-left: 2px;
          }
          &>i {
            right: 0;
            margin-right: 5px !important;
          }
    }
}
ul:has(.top-nav-submenu) {
    min-width: 135px !important;
    text-align: center;
}
.top-nav-submenu .svg-icon {
    margin-left: 5px;
}
.top-nav-submenu .menu-title {
    margin-left: 5px;
}
</style>