import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { loadLang } from '/@/lang/index'
import { registerIcons, formatTime, formatDate, findOneValue } from '/@/utils/common'
import ElementPlus from 'element-plus'
import mitt from 'mitt'
import pinia from '/@/stores/index'
import { directives } from './directive/directives'
import 'element-plus/dist/index.css'
import '/@/assets/styles/index.scss'
// svg-icon
import 'virtual:svg-icons-register'
import SvgIcon from '/@/components/SvgIcon/index.vue'
// 自定义表格工具组件
import RightToolbar from '/@/components/RightToolbar/index.vue'
// 分页组件
import Pagination from '/@/components/Pagination/index.vue'
import Cookies from 'js-cookie'
// 纠结 import 'default-passive-events' 
// 加了有的组件报错 Unable to preventDefault inside passive event listener invocation.
// 不加控制台则提示 [Violation] Added non-passive event listener to a scroll-blocking 'wheel' event. Consider marking event handler as 'passive' to make the page more responsive. 
    
async function start() {
    const app = createApp(App)

    // 全局方法挂载
    app.config.globalProperties.formatTime = formatTime
    app.config.globalProperties.formatDate = formatDate
    app.config.globalProperties.findOneValue = findOneValue

    // 注册全局组件
    app.component('RightToolbar', RightToolbar)
    // 注册分页组件
    app.component('Pagination', Pagination)
    
    app.use(router)
    app.use(pinia)

    // 全局语言包加载
    const i18n = await loadLang(app)
    app.use(ElementPlus, { 
        i18n: i18n.global.t,
        // 支持 large、default、small
        size: Cookies.get('size') || 'default'
    })

    // 全局注册
    directives(app) // 指令
    registerIcons(app) // icons
    app.component('svg-icon', SvgIcon)
    app.mount('#app')

    app.config.globalProperties.eventBus = mitt()
}
start()
