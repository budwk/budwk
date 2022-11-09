import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { staticRoutes } from '/@/router/static'
import { loading } from '/@/utils/loading'
import { useUserViews } from '../stores/userViews'
import { useUserInfo } from '../stores/userInfo'
import { isExternal } from '../utils/common'
import { setupLayouts } from 'virtual:generated-layouts'
import generatedRoutes from 'virtual:generated-pages'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { USER_INFO,USER_VIEWS,TAGS_VIEW } from '/@/stores/constant/cacheKey'
import { Local } from '/@/utils/storage'
import { ElNotification } from 'element-plus'


const router = createRouter({
    history: createWebHistory(),
    routes: setupLayouts(generatedRoutes.concat(staticRoutes)),
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return { top: 0 }
        }
    },
})


router.beforeEach((to, from, next) => {
    NProgress.configure({ showSpinner: false })
    NProgress.start()
    if (!window.existLoading) {
        loading.show()
        window.existLoading = true
    }
    // 登录页清除缓存 & 初始化平台信息
    if(to.path=='/platform/login'){
        Local.remove(USER_INFO)
        Local.remove(USER_VIEWS)
        Local.remove(TAGS_VIEW)
        usePlatformInfo().init()
    }
    // 获取用户信息、权限角色、菜单
    if(useUserInfo().hasToken()){
        if(!useUserInfo().isLogin()){ //未登录
            useUserInfo().init().then(()=>{
                useUserViews().generateRoutes().then(()=>{
                    const rou = useUserInfo().getRoute(to.path) //获取路由对应的后台菜单信息
                    if(rou){
                        to.meta.title = rou.title
                        to.meta.breadcrumb = rou.breadcrumb
                        to.meta.tree = rou.tree
                        next()
                    } else if(!to.path.startsWith('/redirect') && to.path!='/platform/dashboard'){
                        ElNotification({
                            type: 'error',
                            message: `没有访问 ${to.fullPath} 的权限`,
                        })
                        next('/platform/dashboard')
                    } else {
                        next()
                    }
                })
            })
        }else {
            const rou = useUserInfo().getRoute(to.path) //获取路由对应的后台菜单信息
            if(rou){
                to.meta.title = rou.title
                to.meta.breadcrumb = rou.breadcrumb
                to.meta.tree = rou.tree
                next()
            } else if(!to.path.startsWith('/redirect') && to.path!='/platform/dashboard'){
                ElNotification({
                    type: 'error',
                    message: `没有访问 ${to.fullPath} 的权限`,
                })
                next('/platform/dashboard')
            } else {
                next()
            }
        }
    } else {
        // 重定向到登录页
        if(to.path.startsWith('/platform/') && to.path!='/platform/login'){
            next(`/platform/login?redirect=${to.fullPath}`)
            NProgress.done()
        } else{
            next()
        }
        
    }
})

// 路由加载后
router.afterEach(() => {
    if (window.existLoading) {
        loading.hide()
    }
    NProgress.done()
})

export default router
