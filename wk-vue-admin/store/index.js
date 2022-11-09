import { generateMenus } from '@/utils/common'
import {
  API_AUTH_INFO,
  API_AUTH_SET_THEME
} from '@/constant/api/ucenter/auth'

export const state = () => ({
  auth: null,
  userInfo: null,
  conf: null,
  menus: null,
  roles: null,
  apps: null,
  commonMenus: null,
  menuStatus: {
    activeIndex: '', // 一级菜单激活index
    activeSubmenuIndex: '', // 二级菜单激活index
    isCollapse: false, // 一级菜单是否折叠
    isSubmenuCollapse: false, // 二级菜单是否折叠
    isSubmenuShow: false // 二级菜单是否展示
  },
  themeConfig: {
    theme: '', // 主题颜色
    fixedHeader: false, // 固定头部
    fixedAside: false, // 固定侧边栏
    verticalMenu: true, // 是否是垂直菜单
    contentWidth: 's' // 布局形式
  },
  locale: 'zh-CN',
  localeData: '',
  siteInfo: {
    appId: process.env.PLATFORM_CODE,
    appTitle: process.env.PLATFORM_TITLE,
    appName: process.env.PLATFORM_NAME,
    appVersion: process.env.PLATFORM_VERSION,
    appCopyright: process.env.PLATFORM_COPYRIGHT,
    appPath: process.env.PLATFORM_PATH
  }
})

export const getters = {
  commonMenus: (state) => {
    return generateMenus(state.commonMenus, '')
  },
  userMenus: (state) => {
    return generateMenus(state.menus, '')
  },
  userRoles: (state) => {
    return state.roles
  },
  userPermissionUrl: (state) => {
    const commonMenus = state.commonMenus
      .filter((item) => item.type === 'menu' && item.href !== '')
      .map((item) => item.href)
    const menus = state.menus
      .filter((item) => item.type === 'menu' && item.href !== '')
      .map((item) => item.href)
    return commonMenus.concat(menus)
  },
  userPermissions: (state) => {
    return state.menus.map((item) => item.permission)
  },
  userAuth: (state) => {
    return state.auth
  }
}

export const mutations = {
  setAuth(state, auth) {
    state.auth = auth
  },
  setUserInfo(state, userInfo) {
    state.userInfo = userInfo
  },
  setCommonMenus(state, commonMenus) {
    state.commonMenus = commonMenus
  },
  setMenus(state, menus) {
    state.menus = menus
  },
  setApps(state, apps) {
    state.apps = apps
  },
  setRoles(state, roles) {
    state.roles = roles
  },
  setConf(state, conf) {
    state.conf = conf
  },
  setMenuStatus(state, payload) {
    state.menuStatus = {
      ...state.menuStatus,
      ...payload
    }
  },
  setThemeConfig(state, payload) {
    state.themeConfig = {
      ...state.themeConfig,
      ...payload
    }
  },
  setLocale(state, payload) {
    state.locale = payload
  },
  logout(state) {
    state.auth = null
  }
}

export const actions = {
  setMenuStatus({ commit, state }, context) {
    commit('setMenuStatus', context)
    this.$cookies.set('menuStatus', JSON.stringify(state.menuStatus))
  },
  setThemeConfig({ commit, state }, context) {
    commit('setThemeConfig', context)
    try {
      this.$axios.$post(API_AUTH_SET_THEME, {
        themeConfig: JSON.stringify(state.themeConfig)
      })
    } catch (e) {
      console.log(e)
    }
  },
  setLocale({ commit, state }, context) {
    commit('setLocale', context)
    this.$cookies.set('locale', state.locale)
  },
  logout({ commit }) {
    commit('logout')
    this.$cookies.remove('auth')
    this.$cookies.remove('wk-user-token')
  },
  async globalInit({ commit }, context) {
    // 页面权限验证是通过store.state.auth进行判断的,这里登陆后页面每次获取并设置新store.state.auth值
    if (this.$cookies.get('wk-user-token')) {
      try {
        const { data } = await this.$axios.$get(API_AUTH_INFO, { params: { appId: this.$cookies.get('appId') }})
        commit('setAuth', data.token.tokenValue)
        commit('setUserInfo', data)
        commit('setConf', data.conf)
        commit('setApps', data.apps)
        var commonMenus = data.menus['COMMON']
        var selfMenus = data.menus[this.$cookies.get('appId')]
        commit('setMenus', selfMenus)
        commit('setCommonMenus', commonMenus)
        commit('setRoles', data.roles)
        if (data.user.themeConfig) {
          // 同步全局主题配置
          const dbThemeConfig = JSON.parse(data.user.themeConfig)
          commit('setThemeConfig', {
            theme: dbThemeConfig.theme, // 主题配色
            fixedHeader: dbThemeConfig.fixedHeader, // 固定头部
            fixedAside: dbThemeConfig.fixedAside, // 固定侧边栏
            verticalMenu: dbThemeConfig.verticalMenu, // 是否是垂直菜单
            contentWidth: dbThemeConfig.contentWidth, // 布局形式
            autoCloseSub: dbThemeConfig.autoCloseSub // 是否自动关闭二级菜单
          })
        }
      } catch (e) {
        commit('logout')
        return context.redirect('/')
      }
    }
  },
  async nuxtClientInit({ dispatch }, context) {
    await dispatch('globalInit', context)
  },
  async nuxtServerInit({ dispatch }, context) {
    await dispatch('globalInit', context)
  }
}
