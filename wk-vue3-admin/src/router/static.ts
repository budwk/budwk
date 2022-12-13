import { RouteRecordRaw } from 'vue-router'
import PlatformLayout from '/@/layouts/platform/index.vue'

const pageTitle = (name: string): string => {
    return `pagesTitle.${name}`
}

/*
 * 静态路由
 */
const staticRoutes: Array<RouteRecordRaw> = [
    {
        // 首页
        path: '/',
        redirect: '/platform/login'
    },
    {
        // 管理员登录页
        path: '/platform/login',
        component: () => import('/@/views/platform/login.vue'),
        meta: {
            title: pageTitle('platformLogin'),
        },
    },
    {
        path: '/redirect/:path(.*)',
        component: () => import('/@/views/redirect/index.vue'),
        meta: {
            layout: "platform/index"
        }
    },
    {
        // 管理员首页
        path: '/platform',
        redirect: '/platform/dashboard'
    },
    {
        // 无权限访问
        path: '/401',
        component: () => import('/@/views/common/error/401.vue'),
        meta: {
            title: pageTitle('noPower'),
        },
    },
    {
        path: '/:path(.*)*',
        redirect: '/404',
    },
    {
        // 404
        path: '/404',
        component: () => import('/@/views/common/error/404.vue'),
        meta: {
            title: pageTitle('notFound'), // 页面不存在
        },
    },
    {
        // 后台找不到页面了-可能是路由未加载上
        path: '/platform:path(.*)*',
        redirect: (to) => {
            return { path: '/platform/loading', query: { url: to.path, query: JSON.stringify(to.query) } }
        },
    },
]

/*
 * 后台基础静态路由
 */
const platformBaseRoute: RouteRecordRaw = {
    path: '/platform/loading',
    component: () => import('/@/layouts/platform/index.vue'),
    redirect: '/platform/loading',
    children: [
        {
            path: '/platform/loading',
            component: () => import('/@/layouts/platform/components/Loading/index.vue'),
            meta: {
                title: '加载中...',
            },
        },
    ],
}

const platformDashboard: RouteRecordRaw = {
    // 控制台
    path: '/platform/dashboard',
    name: 'platform-dashboard',
    component: () => import('/@/views/platform/dashboard.vue'),
    meta: {
        title: '控制台',
        affix: true,
        layout: "platform/index"
    },
}

staticRoutes.push(platformBaseRoute)

export { staticRoutes,platformDashboard }
