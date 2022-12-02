<template>
    <div class="navbar">
      <hamburger id="hamburger-container" :is-active="client.sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
      <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!settingsStore.topNav" />
      <top-nav id="topmenu-container" class="topmenu-container" v-if="settingsStore.topNav" />
  
      <div class="right-menu">
        <template v-if="client.device !== 'mobile'">
          <app-select id="app-select" class="right-menu-item" />
          <el-tooltip content="菜单检索" effect="dark" placement="bottom">
            <header-search id="header-search" class="right-menu-item" />
          </el-tooltip>
          <el-tooltip content="刷新" effect="dark" placement="bottom">
            <reload id="reload" class="right-menu-item hover-effect" />
          </el-tooltip>
          <el-tooltip content="全屏" effect="dark" placement="bottom">
            <screenfull id="screenfull" class="right-menu-item hover-effect" />
          </el-tooltip>
          <el-tooltip content="布局大小" effect="dark" placement="bottom">
            <size-select id="size-select" class="right-menu-item hover-effect" />
          </el-tooltip>
          <notice id="notice" class="right-menu-item hover-effect"></notice>
        </template>          
        <el-popover
            @show="onCurrentNavMenu(true, 'userInfo')"
            @hide="onCurrentNavMenu(false, 'userInfo')"
            placement="bottom-end"
            :hide-after="0"
            :width="260"
            trigger="click"
            popper-class="user-info-box"
        >
            <template #reference>
                <div class="user-info" :class="currentNavMenu == 'userInfo' ? 'hover' : ''">
                    <el-avatar :size="25" fit="fill">
                      <img v-if="userInfo.user.avatar" :src="platformInfo.AppFileDomain + userInfo.user.avatar" alt="" />
                      <img v-else src="~assets/images/avatar.png" alt="" />
                    </el-avatar>
                    <div class="user-name">{{ userInfo.user.username }}</div>
                </div>
            </template>
            <div>
                <div class="user-info-base">
                    <el-avatar :size="70" fit="fill">
                        <img v-if="userInfo.user.avatar" :src="platformInfo.AppFileDomain + userInfo.user.avatar" alt="" />
                        <img v-else src="~assets/images/avatar.png" />
                    </el-avatar>
                    <div class="user-info-other">
                        <div class="user-info-name">{{ userInfo.user.username }}</div>
                        <div class="user-info-lasttime">{{ formatTime(userInfo.user.loginAt) }}</div>
                    </div>
                </div>
                <div class="user-info-footer">
                    <el-button type="primary" @click="goToUserHome" plain>用户资料</el-button>
                    <el-button type="danger" @click="logout" plain>注销登录</el-button>
                </div>
            </div>
        </el-popover>
        <el-tooltip content="界面风格" effect="dark" placement="bottom">
          <div class="right-menu-item settings" @click="setLayout">
            <svg-icon class-name="size-icon" icon-class="settings"/>
          </div>
        </el-tooltip>
      </div>
    </div>
  </template>
  
<script setup lang="ts">
import { ElMessageBox } from 'element-plus'
import Breadcrumb from '/@/components/Breadcrumb/index.vue'
import Reload from '/@/components/Reload/index.vue'
import TopNav from '../TopNav/index.vue'
import Hamburger from '/@/components/Hamburger/index.vue'
import Screenfull from '/@/components/Screenfull/index.vue'
import SizeSelect from '/@/components/SizeSelect/index.vue'
import AppSelect from '/@/components/AppSelect/index.vue'
import HeaderSearch from '/@/components/HeaderSearch/index.vue'
import Notice from '/@/components/Notice/index.vue'
import { formatTime } from '/@/utils/common'
import { useClient } from '/@/stores/client'
import { useUserInfo } from '/@/stores/userInfo'
import { usePlatformInfo } from "/@/stores/platformInfo"
import { useUserSettings } from '/@/stores/userSettings'
import { ref } from 'vue'
import router from '/@/router'  

const client = useClient()
const userInfo = useUserInfo()
const platformInfo = usePlatformInfo()
const settingsStore = useUserSettings()
const currentNavMenu = ref('')
  
const onCurrentNavMenu = (status: boolean, name: string) => {
    currentNavMenu.value = status ? name : ''
}

const toggleSideBar = () => {
    client.toggleSideBar()
}
  
const goToUserHome = () => {
    router.push('/platform/home/user')
}
  
const logout = () => {
    ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        useUserInfo().logout()
    }).catch(() => { })
}
  
const emits = defineEmits(['setLayout'])
const setLayout = () => {
    emits('setLayout');
}
</script>
  
  <style lang='scss' scoped>
  .navbar {
    height: 50px;
    overflow: hidden;
    position: relative;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  
    .hamburger-container {
      line-height: 46px;
      height: 100%;
      float: left;
      cursor: pointer;
      transition: background 0.3s;
      -webkit-tap-highlight-color: transparent;
  
      &:hover {
        background: rgba(0, 0, 0, 0.025);
      }
    }
  
    .breadcrumb-container {
      float: left;
    }
  
    .topmenu-container {
      position: absolute;
      left: 50px;
    }
  
    .errLog-container {
      display: inline-block;
      vertical-align: top;
    }
  
    .right-menu {
      float: right;
      height: 100%;
      line-height: 50px;
      display: flex;
  
      &:focus {
        outline: none;
      }
  
      .right-menu-item {
        display: inline-block;
        padding: 0 8px;
        height: 100%;
        font-size: 18px;
        color: #5a5e66;
        vertical-align: text-bottom;
  
        &.hover-effect {
          cursor: pointer;
          transition: background 0.3s;
  
          &:hover {
            background: rgba(0, 0, 0, 0.025);
          }
        }
      }
  
      .user-info {
        display: flex;
        height: 100%;
        padding: 0 10px;
        align-items: center;
        cursor: pointer;
        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
        user-select: none;
      }
      .user-name {
          font-size: 12px;
          padding-left: 6px;
          white-space: nowrap;
      }
    }
    .settings {
      cursor: pointer;
      &:hover{
      .svg-icon{
        animation: twinkle 0.3s ease-in-out;
      }
    }
    }
  }


.user-info-base {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    padding-top: 10px;
    .user-info-other {
        display: block;
        width: 100%;
        text-align: center;
        padding: 10px 0;
        .user-info-name {
            font-size: var(--el-font-size-medium);
        }.user-info-lasttime {
            font-size: var(--el-font-size-small);
        }
    }
}
.user-info-footer {
    padding: 10px 0;
    margin: 0 -12px -12px -12px;
    display: flex;
    justify-content: space-around;
}
  </style>
  