const Cookie = process.browser ? require('js-cookie') : undefined
export default ({ app, store }) => {
  if (process.browser) {
    app.router.afterEach(async(to, from) => {
      if (store.state.userInfo) {
        const userInfo = store.state.userInfo
        // 查找路径对应一级菜单的index
        const url = to.path.split('/')[1]
        const index = userInfo.menus.findIndex((item) => {
          return item.identify === url
        })

        // 同步菜单配置
        if (Cookie.get('menuStatus')) {
          const cookieMenuStatus = JSON.parse(
            Cookie.get('menuStatus')
          )
          store.dispatch('setMenuStatus', {
            activeIndex: index !== -1 ? index.toString() : '', // 一级菜单激活index
            activeSubmenuIndex: cookieMenuStatus.activeSubmenuIndex, // 二级菜单激活index
            isCollapse: cookieMenuStatus.isCollapse, // 一级菜单是否折叠
            isSubmenuCollapse: cookieMenuStatus.isSubmenuCollapse, // 二级菜单是否折叠
            isSubmenuShow: cookieMenuStatus.isSubmenuShow // 二级菜单是否展示
          })
        }

        // 同步全局主题配置
        if (Cookie.get('themeConfig')) {
          const cookieThemeConfig = JSON.parse(
            Cookie.get('themeConfig')
          )
          store.commit('setThemeConfig', {
            theme: cookieThemeConfig.theme, // 主题配色
            fixedHeader: cookieThemeConfig.fixedHeader, // 固定头部
            fixedAside: cookieThemeConfig.fixedAside, // 固定侧边栏
            verticalMenu: cookieThemeConfig.verticalMenu, // 是否是垂直菜单
            contentWidth: cookieThemeConfig.contentWidth // 布局形式
          })
        }
      }
    })
  }
}
