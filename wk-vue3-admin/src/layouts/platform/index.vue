<template>
  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <sidebar v-if="!topNav" class="sidebar-container" />
    <div :class="{ hasTagsView: needTagsView, sidebarHide: topNav }" class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <navbar @setLayout="setLayout" />
        <tags-view v-if="needTagsView" />
      </div>
      <app-main />
      <settings ref="settingRef" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { AppMain, TagsView, Settings, Navbar } from './components'
import Sidebar from './components/Sidebar/index.vue'
import { useWindowSize } from '@vueuse/core'
import { computed, onMounted, ref, watchEffect } from 'vue'
import router from '/@/router'
import { useUserInfo } from '/@/stores/userInfo'
import { useUserViews } from '/@/stores/userViews'
import { useUserSettings } from '/@/stores/userSettings'
import { useClient } from '/@/stores/client'
import { useTagsView } from '/@/stores/tagsView'

const userInfo = useUserInfo()
const userSettings = useUserSettings()
const client = useClient()

const theme = computed(() => userSettings.themeColor);
const sideTheme = computed(() => userSettings.sideTheme);
const sideWidth = computed(() => userSettings.sideWidth);
const topNav = computed(() => userSettings.topNav);
const sidebar = computed(() => client.sidebar);
const device = computed(() => client.device);
const needTagsView = computed(() => userSettings.tagsView);
const fixedHeader = computed(() => userSettings.fixedHeader);

const classObj = computed(() => ({
    hideSidebar: !sidebar.value.opened||sidebar.value.hide,
    openSidebar: sidebar.value.opened,
    withoutAnimation: sidebar.value.withoutAnimation,
    mobile: device.value === 'mobile'
}))

const { width, height } = useWindowSize();
const WIDTH = 992; // refer to Bootstrap's responsive design

onMounted(() => {
    if (!userInfo.getToken()) return router.push('/platform/login')
})


watchEffect(() => {
    if (device.value === 'mobile' && sidebar.value.opened) {
        client.closeSideBar({ withoutAnimation: false })
    }
    if (width.value - 1 < WIDTH) {
        client.toggleDevice('mobile')
        client.closeSideBar({ withoutAnimation: true })
    } else {
        client.toggleDevice('desktop')
    }
})

function handleClickOutside() {
    client.closeSideBar({ withoutAnimation: false })
}

const settingRef = ref(null);

function setLayout() {
    settingRef.value.openSetting();
}

</script>

<style lang="scss" scoped>
@import "/@/assets/styles/mixin.scss";
@import "/@/assets/styles/variables.module.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$base-sidebar-width});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}
</style>