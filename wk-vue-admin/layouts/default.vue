<template>
  <div :data-theme="themeConfig.theme">
    <div class="platform">
      <div
        class="platform-header-container"
        :class="fixedHeader ? 'fixed' : ''"
      >
        <div
          class="platform-header-content"
          :class="
            !verticalMenu && contentWidth === 'f'
              ? 'platform-wide'
              : ''
          "
        >
          <div class="platform-header-content-logo">
            <nuxt-link :to="homePath" class="platform-header-content-logo-txt">
              {{ siteInfo.appTitle }}
            </nuxt-link>
            <span v-if="siteInfo.appVersion" class="platform-header-content-logo-version">{{ siteInfo.appVersion }}</span>
            <el-dropdown class="platform-header-content-logo-link" trigger="click" @command="handleChangeApp">
              <div>
                <span>
                  {{ getAppName() }}
                </span>
                <span>
                  <i class="el-icon-arrow-down" />
                </span>
              </div>
              <el-dropdown-menu
                slot="dropdown"
              >
                <template v-for="app in apps">
                  <el-dropdown-item v-if="'COMMON'!==app.id" :key="app.id" :command="app">
                    <img v-if="app.icon" :src="app.icon" style="width:16px;height:16px;"> {{ app.name }}
                  </el-dropdown-item>
                </template>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <!--顶部菜单-->
          <div
            v-if="!verticalMenu"
            class="platform-header-content-menu"
          >
            <HorizontalMenu
              :default-active="menuStatus.activeSubmenuIndex"
              :menu-data="userMenus"
              is-overflow
              @select="handleHorizontalSelect"
            />
          </div>
          <!--顶部菜单结束-->
          <div class="platform-header-content-info">
            <ul class="platform-header-content-info-nav">
              <li class="platform-header-content-info-nav-item">
                <a href="https://budwk.com" target="_blank"><span>官网</span></a>
              </li>
              <li class="platform-header-content-info-nav-item">
                <a href="https://nutz.cn" target="_blank"><span>社区</span></a>
              </li>
              <li class="platform-header-content-info-nav-item">
                <a href="https://gitee.com/budwk/budwk" target="_blank"><span>Gitee</span></a>
              </li>
              <li class="platform-header-content-info-nav-item">
                <a href="https://github.com/budwk/budwk" target="_blank"><span>Github</span></a>
              </li>
              <li class="platform-header-content-info-nav-item">
                <el-popover
                  placement="bottom-end"
                  title="站内消息"
                  width="320"
                  trigger="hover"
                >
                  <el-table :data="notice.list" :show-header="false" :stripe="true" size="mini">
                    <el-table-column :show-overflow-tooltip="true" property="title">
                      <template slot-scope="scope">
                        <span v-if="scope.row.url">
                          <nuxt-link style="outline:none;text-decoration:none;color:#606266;" :to="scope.row.url">{{ scope.row.title }}</nuxt-link>
                        </span>
                        <span v-else>
                          <nuxt-link style="outline:none;text-decoration:none;color:#606266;" :to="'/home/notifications?id='+scope.row.msgId">{{ scope.row.title }}</nuxt-link>
                        </span>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-button type="text" size="mini" style="padding-top:15px;" @click="$router.push('/home/notifications')">
                    更多消息
                  </el-button>
                  <nuxt-link slot="reference" to="/home/notifications">
                    <el-badge :value="notice.size" class="item">
                      <i class="fa fa-bell" /> <span>消息</span>
                    </el-badge>
                  </nuxt-link>
                </el-popover>
              </li>
              <li v-for="menu in commonMenus" :key="menu.id" class="platform-header-content-info-nav-menu">
                <el-dropdown @command="handleCommonMenu">
                  <div
                    class="platform-header-content-info-nav-menu-icon"
                  >
                    <span><i :class="`${menu.icon}`" /></span>
                    <span>{{ menu.name }} </span>
                  </div>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item v-for="subMenu in menu.children" :key="subMenu.id" :command="subMenu.url">
                      <span v-if="subMenu.url">{{ subMenu.name }}</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </li>
              <li class="platform-header-content-info-nav-item">
                <el-dropdown @command="handleCommand">
                  <div
                    class="platform-header-content-info-nav-item-avatar"
                  >
                    <span><i class="fa fa-user" /></span>
                    <span>{{
                      userInfo.user.username
                    }}</span>
                  </div>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="theme">
                      布局样式
                    </el-dropdown-item>
                    <el-dropdown-item command="logout">
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="platform-container">
        <!--侧边菜单-->
        <div
          v-if="verticalMenu"
          :class="
            `platform-aside-wrap
          ${fixedHeader ? 'fixed' : ''}
          ${menuStatus.isCollapse ? 'aside-off' : ''}
          ${
              menuStatus.activeIndex !== '' &&
              menuStatus.isSubmenuShow
                ? 'aside-second-on'
                : 'aside-second-off'
            }`
          "
        >
          <div class="platform-aside">
            <div
              class="platform-aside-collapse-btn"
              @click="toggleMenu"
            >
              <i class="fa fa-sliders fa-color-white" />
            </div>
            <el-menu
              :default-active="
                menuStatus.activeIndex !== ''
                  ? menuStatus.activeIndex
                  : ''
              "
              :collapse="menuStatus.isCollapse"
              :unique-opened="true"
              class="platform-aside-menu"
              @select="handleSelect"
              @open="handleOpen"
              @close="handleClose"
            >
              <template v-for="item in userMenus">
                <el-menu-item
                  :key="item.id"
                  :index="item.path"
                  class="platform-aside-menu-item"
                >
                  <i :class="`${item.icon}`" /><span
                    slot="title"
                  >
                    {{ item.name }}
                  </span>
                </el-menu-item>
              </template>
            </el-menu>
          </div>
          <div
            v-if="
              userMenus
            "
            class="platform-aside-second"
          >
            <div class="platform-aside-second-content">
              <div class="platform-aside-second-title">
                {{ subMenus.name }}
                <i
                  class="el-icon-arrow-left"
                  @click="toggleSubmenu"
                />
              </div>
              <el-menu
                :default-active="menuStatus.activeSubmenuIndex"
                :collapse="menuStatus.isSubmenuCollapse"
                :unique-opened="true"
                class="platform-aside-second-menu"
                @select="handleSubSelect"
              >
                <MenuItem
                  v-for="item in subMenus.children"
                  :key="item.index"
                  :menu-item="item"
                />
              </el-menu>
            </div>
          </div>
        </div>
        <!--侧边菜单结束-->

        <div
          class="platform-content-wrap"
          :class="
            !verticalMenu && contentWidth === 'f'
              ? 'platform-wide'
              : ''
          "
        >
          <nuxt />

          <div class="platform-content-footer">
            <div class="platform-content-footer-logo">
              {{ siteInfo.appTitle }}
            </div>
            <div class="platform-content-footer-copyright">
              {{ siteInfo.appCopyright }}
            </div>
          </div>
        </div>
      </div>

      <!-- 配置 -->

      <Drawer
        :visible.sync="drawerVisible"
        handle
        :handle-class="`platform-${themeConfig.theme}`"
        class="platform-config"
      >
        <div class="platform-config-container">
          <div class="platform-config-block">
            <div class="platform-config-title">
              主题色
            </div>
            <div class="platform-config-body">
              <div class="platform-config-theme-color-lists">
                <div
                  class="platform-config-theme-color-item platform-default"
                  @click="changeThemes('default')"
                />
                <div
                  class="platform-config-theme-color-item platform-theme1"
                  @click="changeThemes('theme1')"
                />
                <div
                  class="platform-config-theme-color-item platform-theme2"
                  @click="changeThemes('theme2')"
                />
                <div
                  class="platform-config-theme-color-item platform-theme3"
                  @click="changeThemes('theme3')"
                />
                <div
                  class="platform-config-theme-color-item platform-theme4"
                  @click="changeThemes('theme4')"
                />
              </div>
            </div>
          </div>

          <div class="platform-config-block">
            <div class="platform-config-title">
              导航设置
            </div>
            <div class="platform-config-body">
              <el-form
                ref="form"
                v-model="themeConfig"
                class="platform-config-form"
              >
                <el-form-item
                  label="菜单布局"
                  class="platform-config-form-item"
                >
                  <el-radio-group
                    v-model="verticalMenu"
                    @change="verticalMenuChange"
                  >
                    <el-radio :label="true">
                      纵向
                    </el-radio>
                    <el-radio :label="false">
                      横向
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item
                  v-if="verticalMenu"
                  label="自动关闭二级菜单"
                  class="platform-config-form-item"
                >
                  <el-radio-group
                    v-model="autoCloseSub"
                  >
                    <el-radio :label="true">
                      是
                    </el-radio>
                    <el-radio :label="false">
                      否
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item
                  label="内容区域宽度"
                  class="platform-config-form-item"
                >
                  <el-select
                    v-model="contentWidth"
                    placeholder="请选择"
                    style="width:140px"
                  >
                    <el-option
                      v-for="item in contentWidthOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item
                  label="固定头部"
                  class="platform-config-form-item"
                >
                  <el-switch v-model="fixedHeader" />
                </el-form-item>
                <el-form-item
                  label="固定侧边栏"
                  class="platform-config-form-item"
                >
                  <el-switch
                    v-model="fixedAside"
                    :disabled="!verticalMenu"
                  />
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </Drawer>
    </div>
  </div>
</template>

<script>
var ws
import { mapState, mapGetters } from 'vuex'
import Drawer from '@/components/Drawer'
import MenuItem from '@/components/NavMenu/menuItem'
import HorizontalMenu from '@/components/NavMenu/horizontalMenu'
import { API_AUTH_LOGOUT } from '@/constant/api/ucenter/auth'
export default {
  filters: {
    fullPathAvatar(avatar) {
      if (avatar) {
        return process.env.FILE_URL + avatar
      }
    }
  },
  components: {
    Drawer,
    MenuItem,
    HorizontalMenu
  },
  data() {
    return {
      drawerVisible: false,
      btnLoading: false,
      messages: [],
      message: '',
      notice: { size: 0, list: [] },
      homePath: ''
    }
  },
  computed: {
    ...mapState({
      conf: (state) => state.conf, // 后台配置参数
      apps: (state) => state.apps, // 应用列表
      siteInfo: (state) => state.siteInfo, // 平台信息
      userInfo: (state) => state.userInfo, // 用户信息
      menuStatus: (state) => state.menuStatus, // 菜单状态
      themeConfig: (state) => state.themeConfig, // 主题配置
      locale: (state) => state.locale, // 多语言
      localeData: (state) => state.localeData // 多语言
    }),
    ...mapGetters(['commonMenus', 'userMenus', 'userRoles']),
    subMenus() {
      return this.userMenus[
        this.menuStatus.activeIndex !== ''
          ? this.menuStatus.activeIndex
          : ''
      ] || {}
    },
    currentLocal: {
      get() {
        return this.userInfo.lang.find(
          (item) => item.locale === this.$store.state.locale
        )
      },
      set() {}
    },
    contentWidthOptions() {
      if (!this.verticalMenu) {
        return [
          {
            label: '定宽',
            value: 'f'
          },
          {
            label: '流式',
            value: 's'
          }
        ]
      } else {
        return [
          {
            label: '流式',
            value: 's'
          }
        ]
      }
    },
    verticalMenu: {
      get() {
        return this.themeConfig.verticalMenu
      },
      set(value) {
        this.$store.dispatch('setThemeConfig', {
          verticalMenu: value
        })
      }
    },
    contentWidth: {
      get() {
        return this.themeConfig.contentWidth
      },
      set(value) {
        this.$store.dispatch('setThemeConfig', {
          contentWidth: value
        })
      }
    },
    fixedHeader: {
      get() {
        return this.themeConfig.fixedHeader
      },
      set(value) {
        this.$store.dispatch('setThemeConfig', {
          fixedHeader: value
        })
      }
    },
    fixedAside: {
      get() {
        return this.themeConfig.fixedAside
      },
      set(value) {
        this.$store.dispatch('setThemeConfig', {
          fixedAside: value
        })
      }
    },
    autoCloseSub: {
      get() {
        return this.themeConfig.autoCloseSub || false
      },
      set(value) {
        this.$store.dispatch('setThemeConfig', {
          autoCloseSub: value
        })
      }
    }
  },
  watch: {
    $route() {
      this.initMenu()
    }
  },
  created() {
    this.initMenu()
    this.checkFristLogin()
    this.wsConnect()
  },
  methods: {
    wsConnect() {
      // 后台配置未启用ws则不进行初始化
      if (!this.conf.AppWebSocket) {
        return false
      }
      ws = new WebSocket(process.env.WS_URL + '/websocket')
      ws.onopen = (event) => {
        ws.send(JSON.stringify({ userId: this.userInfo.user.id, action: 'join', token: this.$cookies.get('X-Token') }))
      }
      ws.onmessage = (event) => {
        var re = JSON.parse(event.data)
        // 接受新消息
        if (re.action === 'offline') { // 账号下线通知
          this.$alert('您的帐号在其他地方登录，您已被迫下线，如果不是您本人操作，请及时修改密码。', '下线通知', {
            confirmButtonText: '确定',
            callback: action => {
              this.logout()
            }
          })
          ws.close()
        } else if (re.action === 'notice') { // 消息通知
          this.notice = re
          if (re.notify && re.size > 0) {
            this.$notify({
              title: '提示',
              message: '您有' + re.size + '条新消息，请查收！',
              duration: 0
            })
          }
        }
      }
      setTimeout(() => {
        this.wsPing()
      }, 1250)
      // 猜猜11624317是啥
      setInterval(this.wsPing, (11624317 / 1000))
      // 以下判断是否关闭浏览器,然后从room里删除
      var _beforeUnload_time = 0
      var _gap_time = 0
      // 是否是火狐浏览器
      var is_fireFox = navigator.userAgent.indexOf('Firefox') > -1
      window.onunload = () => {
        _gap_time = new Date().getTime() - _beforeUnload_time
        if (_gap_time <= 5) {
          ws.send(JSON.stringify({ userId: this.userInfo.user.id, action: 'left', token: this.$cookies.get('X-Token') }))
        } else {
          // 浏览器刷新
        }
      }
      window.onbeforeunload = () => {
        _beforeUnload_time = new Date().getTime()
        if (is_fireFox) { // 火狐关闭执行,但是IE关闭好像不起作用 todo
          ws.send(JSON.stringify({ userId: this.userInfo.user.id, action: 'left', token: this.$cookies.get('X-Token') }))
        }
      }
    },
    wsPing() {
      if (ws) {
        // 获取最新消息通知
        ws.send('{}')
      } else {
        this.wsConnect()
      }
    },
    checkFristLogin() {
      if (this.conf.AppPwdCheck && this.userInfo.user.needChangePwd) {
        this.$alert('首次登录，请修改密码！', '操作提示', {
          confirmButtonText: '确定',
          callback: action => {
            this.$router.push('/home/user?tab=pwd')
          }
        })
      }
    },
    initMenu() {
      this.homePath = this.$cookies.get('appPath')
      // 菜单激活逻辑写在default中了
      // 查找路径对应一级菜单的index
      const url = this.$route.path
      const curMenu = this.$store.state.menus.find((item) => item.href === url)
      var indexPath = ''
      if (curMenu) {
        indexPath = curMenu.path.slice(0, 4)
      }
      // 同步菜单配置
      if (this.$cookies.get('menuStatus')) {
        const cookieMenuStatus = this.$cookies.get('menuStatus')
        this.$store.dispatch('setMenuStatus', {
          activeIndex: indexPath, // 一级菜单激活index
          activeSubmenuIndex: cookieMenuStatus.activeSubmenuIndex, // 二级菜单激活index
          isCollapse: cookieMenuStatus.isCollapse, // 一级菜单是否折叠
          isSubmenuCollapse: cookieMenuStatus.isSubmenuCollapse, // 二级菜单是否折叠
          isSubmenuShow: cookieMenuStatus.isSubmenuShow // 二级菜单是否展示
        })
      }
    },
    toggleMenu() {
      this.$store.dispatch('setMenuStatus', {
        isCollapse: !this.menuStatus.isCollapse
      })
    },
    toggleSubmenu() {
      this.$store.dispatch('setMenuStatus', {
        isSubmenuShow: false
      })
    },
    handleSelect(key) {
      this.$store.dispatch('setMenuStatus', {
        isSubmenuShow: true,
        activeIndex: key
      })
    },
    handleSubSelect(url) {
      this.$store.dispatch('setMenuStatus', {
        isSubmenuShow: !this.autoCloseSub,
        activeSubmenuIndex: url
      })
      this.$router.push(url)
    },
    handleHorizontalSelect(url) {
      if (isNaN(parseInt(url))) {
        this.$router.push(url)
      }
    },
    handleOpen(key, keyPath) {
      console.log(key, keyPath)
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath)
    },
    changeThemes(theme) {
      this.$store.dispatch('setThemeConfig', {
        theme: theme
      })
    },
    verticalMenuChange() {
      if (this.verticalMenu) {
        this.$store.dispatch('setThemeConfig', {
          contentWidth: 's'
        })
      }
    },
    // 获取当前应用名称
    getAppName() {
      const app = this.apps.find((obj) => obj.id === this.$cookies.get('appId'))
      return app.name
    },
    // 切换应用
    handleChangeApp(app) {
      if (app.path) {
        this.$cookies.set('appId', app.id)
        this.$cookies.set('appPath', app.path)
        this.$store.dispatch('globalInit', {})
        this.$router.push(app.path)
      }
    },
    // 用户中心
    handleCommonMenu(path) {
      if (path) { this.$router.push(path) }
    },
    // 用户下拉框
    handleCommand(command) {
      if (command === 'theme') {
        this.drawerVisible = true
      }
      if (command === 'logout') {
        this.logout()
      }
    },
    // 退出登陆
    async logout() {
      try {
        await this.$axios.$get(API_AUTH_LOGOUT)
        this.$store.dispatch('logout', null)
        this.$router.push('/')
      } catch (e) {
        console.log(e)
      }
    }
  }
}
</script>
