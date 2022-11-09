<template>
    <div :class="{ 'has-logo': showLogo }" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
      <Logo v-if="showLogo" :collapse="isCollapse" />
      <el-scrollbar :class="sideTheme" wrap-class="scrollbar-wrapper">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :background-color="sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
          :text-color="sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
          :unique-opened="true"
          :active-text-color="themeColor"
          :collapse-transition="false"
          mode="vertical"
        >
          <SidebarItem
            v-for="(route, index) in sidebarRouters"
            :key="route.tree + '_'+ index"
            :item="route"
          />
        </el-menu>
      </el-scrollbar>
    </div>
  </template>

<script setup lang="ts">
import Logo from './Logo.vue'
import SidebarItem from './SidebarItem.vue'
import variables from '/@/assets/styles/variables.module.scss'
import { useRoute } from 'vue-router'
import { computed } from 'vue';
import { useClient } from '/@/stores/client'
import { useUserInfo } from '/@/stores/userInfo'
import { useUserViews } from '/@/stores/userViews'
import { useUserSettings } from '/@/stores/userSettings'

const sidebarRouters =  computed(() => useUserViews().sidebarRouters)
const showLogo = computed(() => useUserSettings().sidebarLogo)
const sideTheme = computed(() => useUserSettings().sideTheme)
const themeColor = computed(() =>  useUserSettings().themeColor)
const sidebarHide = computed(() =>  useClient().sidebar.hide)
const isCollapse = computed(() => !useClient().sidebar.opened)

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
</script>