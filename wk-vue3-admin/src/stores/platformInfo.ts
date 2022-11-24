import { defineStore } from 'pinia'
import { PLATFORM_INFO } from '/@/stores/constant/cacheKey'
import { PlatformInfo } from '/@/stores/interface'
import { getPlatformInfo } from '/@/api/common'

export const usePlatformInfo = defineStore('platformInfo', {
    state: (): PlatformInfo => {
        return {
            AppSessionOnlyOne: false,
            AppDemoEnv: false,
            AppWebSocket: false,
            AppUploadBase: '',
            AppFileDomain: '',
            AppDomain: '',
            AppName: '',
            AppShrotName: '',
            AppVersion: '',
            AppDefault: ''
        }
    },
    actions: {
        dataFill(state: PlatformInfo) {
            this.$state = { ...this.$state, ...state }
        },
        init() {
            const platformInfo = getPlatformInfo(process.env.BASE_APP_ID)
            platformInfo.then((d)=>{
                this.dataFill(d.data)
            })
        }
    },
    persist: {
        key: PLATFORM_INFO,
    },
})
