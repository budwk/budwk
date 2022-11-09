import { defineStore } from 'pinia'
import { CLIENT_INFO } from '/@/stores/constant/cacheKey'
import Cookies from 'js-cookie'

export const useClient = defineStore('client', {
    state: () => ({
        sidebar: {
            opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
            withoutAnimation: false,
            hide: false,
            width: ''
        },
        device: 'desktop',
        appId: '',
        size: Cookies.get('size') || 'default'
    }),
    actions: {
        toggleSideBar(withoutAnimation: any) {
            if (this.sidebar.hide) {
                return false;
            }
            this.sidebar.opened = !this.sidebar.opened
            this.sidebar.withoutAnimation = withoutAnimation
            if (this.sidebar.opened) {
                Cookies.set('sidebarStatus', 1)
            } else {
                Cookies.set('sidebarStatus', 0)
            }
        },
        closeSideBar(withoutAnimation: any) {
            Cookies.set('sidebarStatus', 0)
            this.sidebar.opened = false
            this.sidebar.withoutAnimation = withoutAnimation
        },
        toggleDevice(device: any) {
            this.device = device
        },
        setSize(size: number) {
            this.size = size;
            Cookies.set('size', size)
        },
        toggleSideBarHide(status: boolean) {
            this.sidebar.hide = status
        },
        toggleSideBarOpen(status: boolean) {
            this.sidebar.hide = status
            this.sidebar.opened = !status
            if (this.sidebar.opened) {
                Cookies.set('sidebarStatus', 1)
            } else {
                Cookies.set('sidebarStatus', 0)
            }
        }
    },
    persist: {
        key: CLIENT_INFO,
    },
})