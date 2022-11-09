import { defineStore } from 'pinia'
import { USER_SETTINGS } from '/@/stores/constant/cacheKey'
import { UserSettings } from '/@/stores/interface'

export const useUserSettings = defineStore('userSettings',{
    state: (): UserSettings => {
        return {
            title: '',
            themeColor: '#1E90FF',
            sideTheme: 'theme-dark',
            sideWidth: '320px',
            showSettings: false,
            topNav: false,
            tagsView: true,
            fixedHeader: false,
            sidebarLogo: true,
            dynamicTitle: true,
            defaultLang: 'zh-cn'
        }
    },
    actions: {
        dataFill(state: UserSettings) {
            this.$state = state
        },
        changeSetting(data: any) {
            const { key, value } = data
            if (this.hasOwnProperty(key)) {
                this[key] = value
            }
        },
        setTitle(title: string) {
            this.title = title
        },
        setLang(lang: string) {
            this.defaultLang = lang
        }
    },
    persist: {
        key: USER_SETTINGS,
    }
})