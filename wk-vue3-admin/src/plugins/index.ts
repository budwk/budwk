import vue from '@vitejs/plugin-vue'

import createAutoImport from './auto-import'
import createSvgIcon from './svg-icon'
import createCompression from './compression'
import createSetupExtend from './setup-extend'
import createPages from './pages'
import createLoyouts from './layouts'

export default function createVitePlugins(env: any, isProd = false) {
    const vitePlugins = [vue()]
    vitePlugins.push(createAutoImport())
	vitePlugins.push(createSetupExtend())
    vitePlugins.push(createSvgIcon(isProd))
    vitePlugins.push(createPages())
    vitePlugins.push(createLoyouts())
	isProd && vitePlugins.push(...createCompression(env))
    return vitePlugins
}
