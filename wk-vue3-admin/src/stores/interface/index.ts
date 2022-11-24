// 变量名对应含义请在 /stores/* 里边找
import { RouteRecordRaw, RouteLocationNormalized } from 'vue-router'

export interface UserSettings {
    title: string
    themeColor: string
    sideTheme: string
    sideWidth: string
    showSettings: boolean
    topNav: boolean
    tagsView: boolean
    fixedHeader: boolean
    sidebarLogo: boolean
    dynamicTitle: boolean
    defaultLang: string
}

export interface UserViews {
    routes: any[]
    addRoutes: any[]
    defaultRoutes: any[]
    topbarRouters: any[]
    sidebarRouters: any[]
    commonRouters: any[]
}

export interface PlatformInfo {
    AppSessionOnlyOne: boolean
    AppDemoEnv: boolean
    AppWebSocket: boolean
    AppUploadBase: string
    AppFileDomain: string
    AppDomain: string
    AppName: string
    AppShrotName: string
    AppVersion: string,
    AppDefault: string
}

export interface UserInfo {
    user: {
        id: string
        username: string
        loginname: string
        email: string
        mobile: string
        avatar: string
        loginAt: number
        loginIp: string
        themeConfig: string
    },
    conf: Map<string,any>
    apps: string[]
    menus: any
    token: string
    tokenValue: string
    roles: string[]
    permissions: string[]
    routes: any,
    selfMenus: any[],
    commonMenus: any[]
}
