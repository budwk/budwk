<template>
  <div class="app-select">
    <el-dropdown class="app-select-dropdown" trigger="click" @command="handleSetApp">
      <span class="el-dropdown-link">
        {{ appName }}
        <el-icon class="el-icon--right">
          <arrow-down />
        </el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <template v-for="item of apps">
            <el-dropdown-item v-if="!item.hidden" :key="item.id" :command="item.id">
              {{ item.name }}
            </el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>
  
<script setup lang="ts">
import { computed } from 'vue';
import { useClient } from '/@/stores/client'
import { useUserInfo } from '/@/stores/userInfo'
import { useUserViews } from '/@/stores/userViews'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { useTagsView } from '/@/stores/tagsView'
import modal from '/@/utils/modal'
import router from '/@/router/index'

const userInfo = useUserInfo()
const apps = computed(() => userInfo.apps);
const appName = computed(() => getAppName(useClient().appId ? useClient().appId : usePlatformInfo().AppDefault))

const getAppName = (id: string) => {
    if (apps.value && apps.value.length > 0) {
        var index = apps.value.findIndex(obj => obj.id === id)
        if (apps.value[index]) {
            return apps.value[index].name
        }
    }
    return ''
}

const getAppPath = (id: string) => {
    if (apps.value && apps.value.length > 0) {
        var index = apps.value.findIndex(obj => obj.id === id)
        if (apps.value[index]) {
            return apps.value[index].path
        }
    }
    return ''
}

const handleSetApp = (id: string) => {
    if (useClient().appId == id) {
        return
    }
    useClient().appId = id
    modal.loading("正在切换应用，请稍候...");
    useUserInfo().init().then(() => {
        useUserViews().generateRoutes().then(() => {
            useTagsView().delAllViews().then(() => {
                router.replace({ path: getAppPath(id) })
                setTimeout(() => {
                    modal.closeLoading()
                }, 500);
            })
        })
    })
}
</script>
  
<style lang='scss' scoped>
.app-select {
  cursor: pointer;

  .app-select-dropdown {
    line-height: 50px;
  }
}
</style>