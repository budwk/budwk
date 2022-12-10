import Pages from  'vite-plugin-pages'

export default function createPages() {
    return Pages({
        dirs:[ { dir: "src/views", baseRoute: "" }],
        importMode: "async",
        exclude: ['**/components/*.vue','**/views/platform/dashboard.vue','**/views/platform/login.vue']
    })
}
