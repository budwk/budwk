import { ComponentInternalInstance, getCurrentInstance } from 'vue'

export default function useCurrentInstance() {
    if (!getCurrentInstance()) {
        throw new Error('useCurrentInstance() can only be used inside setup() or functional components!')
    }
    const { appContext } = getCurrentInstance() as any
    const proxy = appContext.config.globalProperties
    return {
        proxy,
    }
}
