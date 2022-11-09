
import { defineStore } from 'pinia'
import { platformDashboard } from '/@/router/static'
import router from '/@/router/index'
import { USER_VIEWS } from '/@/stores/constant/cacheKey'
import { UserViews} from '/@/stores/interface'
import PlatformLayout from '/@/layouts/platform/index.vue'
import { isExternal } from '/@/utils/common'
import { useUserInfo } from '/@/stores/userInfo'


export const useUserViews = defineStore('userViews', {
    state: () : UserViews => ({
        routes: [],
        addRoutes: [],
        defaultRoutes: [],
        topbarRouters: [],
        sidebarRouters: [],
        commonRouters: []
    }),
    actions: {
        setRoutes(routes:any) {
            this.addRoutes = routes
            this.routes = routes.concat(platformDashboard)
        },
        setDefaultRoutes(routes:any) {
            this.defaultRoutes = routes.concat(platformDashboard)
        },
        setTopbarRoutes(routes:any) {
            this.topbarRouters = routes
        },
        setSidebarRouters(routes:any) {
            this.sidebarRouters = routes
        },
        setCommonRouters(routes:any) {
            this.commonRouters = routes
        },
        generateRoutes() {
            return new Promise(resolve => {
                const userInfo = useUserInfo()
                const selfMenus = userInfo.getSelfMenus()
                const commonMenus =userInfo.getCommonMenus()
                const sidebarRoutesTmp = JSON.parse(JSON.stringify(selfMenus))
                const rewriteRoutesTmp = JSON.parse(JSON.stringify(selfMenus))
                const defaultRoutesTmp = JSON.parse(JSON.stringify(selfMenus))
                const ccommonMenusTmp = JSON.parse(JSON.stringify(commonMenus))
                this.setRoutes(rewriteRoutesTmp)
                this.setSidebarRouters(sidebarRoutesTmp)
                this.setDefaultRoutes(defaultRoutesTmp)
                this.setTopbarRoutes(defaultRoutesTmp)
                this.setCommonRouters(ccommonMenusTmp)
                resolve({})
            })
        }
    },
    persist: {
        key: USER_VIEWS,
    },
})
