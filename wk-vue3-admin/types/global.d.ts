interface Window {
    existLoading: boolean
    lazy: NodeJS.Timer
    unique: number
    tokenRefreshing: boolean
    requests: Function[]
    eventSource: EventSource
}

// Vue 全局属性类型声明
declare module 'vue' {
    interface ComponentCustomProperties {
        formatTime: (dateTime?: string | number | null, fmt?: string) => string
        formatDate: (dateTime?: string | number | null, fmt?: string) => string
        findOneValue: (list: any[], key: string, keyVal: any, valKey: string) => any
        formatField: (data: any, field: string, defaultVal?: string) => string
    }
}

interface anyObj {
    [key: string]: any
}

interface TableDefaultData<T = any> {
    list: T
    remark: string
    total: number
}

interface ApiResponse<T = any> {
    code: number
    data: T
    msg: string
    time: number
}

type ApiPromise<T = any> = Promise<ApiResponse<T>>
