<template>
    <div>
        <div
            v-loading="true"
            element-loading-background="var(--ba-bg-color-overlay)"
            :element-loading-text="$t('utils.Loading')"
            class="default-main ba-main-loading"
        ></div>
        <div v-if="state.showReload" class="loading-footer">
            <el-button @click="refresh" type="warning">{{ $t('utils.Reload') }}</el-button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onUnmounted, reactive } from 'vue'
import router from '/@/router/index'
import { useTagsView } from '/@/stores/tagsView'
import { isPlatformApp } from '/@/utils/common'
import { getFirstRoute, routePush } from '/@/utils/common'
let timer: NodeJS.Timer

const tagsView = useTagsView()
const state = reactive({
    maximumWait: 1000 * 6,
    showReload: false,
})

const refresh = () => {
    router.go(0)
}

if (isPlatformApp() && tagsView.cachedViews) {
    let firstRoute = getFirstRoute(tagsView.cachedViews)
    if (firstRoute) routePush(firstRoute)
}

timer = setTimeout(() => {
    state.showReload = true
}, state.maximumWait)

onUnmounted(() => {
    clearTimeout(timer)
})
</script>

<style scoped lang="scss">
.ba-main-loading {
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
}
.loading-footer {
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
