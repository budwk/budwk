<template>
    <el-config-provider :locale="lang">
        <router-view></router-view>
    </el-config-provider>
</template>
<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import iconfontInit from '/@/utils/iconfont'
import { useRoute } from 'vue-router'
import { setTitleFromRoute } from '/@/utils/common'
import { handleThemeStyle } from '/@/utils/theme'
import { useUserSettings } from '/@/stores/userSettings'

const userSettings = useUserSettings()
const route = useRoute()

// 初始化 element 的语言包
const { getLocaleMessage } = useI18n()
const lang = getLocaleMessage(userSettings.defaultLang) as any
onMounted(() => {
    iconfontInit()
    handleThemeStyle(userSettings.themeColor)
})

// 监听路由变化时更新浏览器标题
watch(
    () => route.path,
    () => {
        setTitleFromRoute()
    }
)
</script>
