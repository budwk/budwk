import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import type { UserConfig, ConfigEnv, ProxyOptions } from 'vite'
import { isProd, loadEnv } from '/@/utils/vite'
import createVitePlugins from '/@/plugins'

const pathResolve = (dir: string): any => {
    return resolve(__dirname, '.', dir)
}

// https://vitejs.cn/config/
const viteConfig = ({ mode }: ConfigEnv): UserConfig => {
    const env = loadEnv(mode)
    const { VITE_PORT, VITE_OPEN, VITE_BASE_PATH, VITE_OUT_DIR, VITE_PROXY_URL } = env

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
        plugins: [createVitePlugins(env, isProd(mode))],
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
            chunkSizeWarningLimit: 1500
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
