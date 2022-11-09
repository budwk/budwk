import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import type { UserConfig, ConfigEnv, ProxyOptions } from 'vite'
import { isProd, loadEnv } from '/@/utils/vite'
import { svgBuilder } from './src/components/Icon/svg/index'
import Pages from  'vite-plugin-pages'
import Layouts from 'vite-plugin-vue-layouts'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'

const pathResolve = (dir: string): any => {
    return resolve(__dirname, '.', dir)
}

// https://vitejs.cn/config/
const viteConfig = ({ mode }: ConfigEnv): UserConfig => {
    const { VITE_PORT, VITE_OPEN, VITE_BASE_PATH, VITE_OUT_DIR, VITE_PROXY_URL } = loadEnv(mode)

    const alias: Record<string, string> = {
        '/@': pathResolve('./src/'),
        assets: pathResolve('./src/assets'),
        'vue-i18n': isProd(mode) ? 'vue-i18n/dist/vue-i18n.cjs.prod.js' : 'vue-i18n/dist/vue-i18n.cjs.js',
    }

    let proxy: Record<string, string | ProxyOptions> = {}
    if (VITE_PROXY_URL) {
        proxy = {
            '/': {
                target: VITE_PROXY_URL,
                changeOrigin: true,
            },
        }
    }

    return {
        define: {
            "process.env": process.env
        },
        plugins: [vue(), 
            
            Pages({
                dirs:[ { dir: "src/views", baseRoute: "" }],
                importMode: "async",
                exclude: ['**/components/*.vue','**/views/platform/dashboard.vue','**/views/platform/login.vue']
            }),
            Layouts({
                layoutsDirs: 'src/layouts',
                defaultLayout: 'default',
                exclude: ['**/components/*.vue']
            }),
            createSvgIconsPlugin({
                iconDirs: [pathResolve('src/assets/icons/svg')],
                symbolId: 'icon-[dir]-[name]',
                svgoOptions: isProd(mode)
            })
        ],
        root: process.cwd(),
        resolve: { alias },
        base: VITE_BASE_PATH,
        server: {
            host: '0.0.0.0',
            port: VITE_PORT,
            open: VITE_OPEN,
            proxy: proxy,
        },
        build: {
            sourcemap: false,
            outDir: VITE_OUT_DIR,
            emptyOutDir: true,
            chunkSizeWarningLimit: 1500,
        },
        css: {
            postcss: {
                plugins: [
                    {
                        postcssPlugin: 'internal:charset-removal',
                        AtRule: {
                            charset: (atRule) => {
                                if (atRule.name === 'charset') {
                                    atRule.remove()
                                }
                            },
                        },
                    },
                ],
            },
        },
    }
}

export default viteConfig
