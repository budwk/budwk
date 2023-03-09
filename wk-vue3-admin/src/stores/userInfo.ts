import { defineStore } from 'pinia'
import { TAGS_VIEW, USER_INFO, USER_VIEWS } from '/@/stores/constant/cacheKey'
import { UserInfo } from '/@/stores/interface'
import { logout, getUserInfo } from '../api/ucenter/auth'
import { Local } from '/@/utils/storage'
import { nextTick, ref } from 'vue'
import { useUserSettings } from './userSettings'
import { usePlatformInfo } from './platformInfo'
import { useClient } from './client'
import { useUserViews } from './userViews'
import { isExternal } from '/@/utils/common'
import router from '../router'
import { staticRoutes } from '/@/router/static'

export const useUserInfo = defineStore('userInfo', {
    state: (): UserInfo => {
        return {
            user: {
                id: '',
                username: '',
                loginname: '',
                email: '',
                mobile: '',
                avatar: '',
                loginAt: 0,
                loginIp: '',
                themeConfig: ''
            },
            conf: new Map<string, any>(),//包含非open的系统参数
            apps: [],
            menus: {},
            token: '',
            tokenValue: '',
            roles: [],
            permissions: [],
            routes: {},
            selfMenus: [],
            commonMenus: []
        }
    },
    actions: {
        dataFill(state: UserInfo) {
            this.$state = state
        },
        setToken(token: string) {
            this.tokenValue = token
        },
        removeToken() {
            this.tokenValue = ''
        },
        getToken() {
            return this.tokenValue
        },
        getSelfMenus():any[] {
            return this.selfMenus
        },
        getCommonMenus():any[] {
            return this.commonMenus
        },
        setRoutes(obj: any): void {
            this.routes = Object.assign(this.routes,obj)
        },
        getRoute(path: string): any {
            return this.routes[path]
        },
        initUserSettings() {
            if(!this.user.themeConfig)
                return
            useUserSettings().dataFill(JSON.parse(this.user.themeConfig))  
        },
        init() {
            // 初始化用户信息,主题样式、角色、权限、菜单、系统参数（含非公开）等
            return new Promise<void>((resolve, reject) => {
                let appId = useClient().appId
                if(!appId) {
                    appId = process.env.BASE_APP_ID || ''
                }            
                getUserInfo().then((res) => {
                    console.log(res)
                    this.dataFill(res.data)
                    nextTick(()=>{
                        this.initUserSettings()
                        this.selfMenus = this.generateMenus(this.menus[appId],'','')
                        this.commonMenus = this.generateMenus(this.menus['COMMON'],'','')
                        this.addStaticRoutes(staticRoutes)
                        resolve()
                    })
                }).catch(error => {
                    reject(error)
                })
            })
        },
        addStaticRoutes(data: any) {
            data.forEach((item: any) => {
                const obj = {}
                obj[item.path] = {}
                this.setRoutes(obj)
                if(item.children){
                    this.addStaticRoutes(item.children)
                }
            })
        },
        generateMenus(data: any, pid: string, pname: string) : any[]{
            const arr:any = []
            let tree:any = {}
            const obj = {}
            data.forEach((item: any) => {
                if (item.parentId === pid && item.type === 'menu') {
                    let breadcrumb = item.name
                    if(pname){
                        breadcrumb = pname + "|" + breadcrumb
                    }
                    const children = this.generateMenus(data, item.id, breadcrumb)
                    if(item.href&&!isExternal(item.href)){
                        obj[item.href] = {
                            permission: item.permission,
                            title: item.name,
                            alias: item.alias,
                            breadcrumb: breadcrumb,
                            tree: item.path
                        }
                    }
                    const menuItem = {
                        name: item.path,
                        path: item.href?item.href:'',
                        children: children,
                        meta: {
                            title: item.name,
                            icon: item.icon,
                            link: isExternal(item.href)?item.href:''
                        },
                        tree: item.path,
                        type: item.type,
                        hidden: !item.showit,
                        fpath: item.href?item.href:'',
                        breadcrumb: breadcrumb
                    }
                    tree = menuItem
                    if(item.showit){ //显示的菜单
                        arr.push(tree)
                    }
                }
            })
            this.setRoutes(obj)
            return arr
        },
        logout(): void{
            logout().then((res: any) => {
                if (res.code == 0) {
                    Local.remove(USER_INFO)
                    Local.remove(USER_VIEWS)
                    Local.remove(TAGS_VIEW)
                    router.go(0)
                }
            })
        },
        logoutNotLogin(): void{
            Local.remove(USER_INFO)
            Local.remove(USER_VIEWS)
            Local.remove(TAGS_VIEW)
            router.go(0)
        },
        hasToken() {
            return !!this.tokenValue
        },
        isLogin() {
            return this.tokenValue && this.token && this.roles.length > 0
        },
    },
    persist: {
        key: USER_INFO,
    },
})