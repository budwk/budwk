import Pages from 'vite-plugin-pages'

export default function createPages() {
    return Pages({
        dirs: [{ dir: "src/views", baseRoute: "" }],
        importMode: "async",
        exclude: ['**/components/*.vue', '**/views/platform/dashboard.vue', '**/views/platform/login.vue'],
        extendRoute(route) {
            // platform 目录下的页面自动使用 platform/index 布局
            if (route.path.startsWith('/platform/') && route.path !== '/platform/login') {
                return {
                    ...route,
                    meta: { ...route.meta, layout: 'platform/index' }
                }
            }
            return route
        }
    })
}
