import axios, { AxiosPromise, Method } from 'axios'
import type { AxiosRequestConfig } from 'axios'
import { ElLoading, LoadingOptions, ElNotification, ElMessageBox } from 'element-plus'
import { useUserSettings } from '/@/stores/userSettings'
import { useUserInfo } from '/@/stores/userInfo'
import { isPlatformApp } from './common'
import router from '/@/router/index'
import { TAGS_VIEW, USER_INFO,USER_SETTINGS,USER_VIEWS } from '/@/stores/constant/cacheKey'
import { i18n } from '/@/lang/index'
import { Local } from '/@/utils/storage'

window.requests = []
window.tokenRefreshing = false
const pendingMap = new Map()
const loadingInstance: LoadingInstance = {
    target: null,
    count: 0,
}
export const isRelogin = { show: false }

export const getUrl = (): string => {
    const value: string = import.meta.env.VITE_AXIOS_BASE_URL as string
    return value == 'getCurrentDomain' ? window.location.protocol + '//' + window.location.host : value
}

export const getUrlPort = (): string => {
    const url = getUrl()
    return new URL(url).port
}

/*
 * 创建Axios
 * 默认开启`reductDataFormat(简洁响应)`,返回类型为`ApiPromise`
 * 关闭`reductDataFormat`,返回类型则为`AxiosPromise`
 */
function createAxios(axiosConfig: AxiosRequestConfig, options: Options = {}, loading: LoadingOptions = {}): ApiPromise | AxiosPromise {
    const userSettings = useUserSettings()
    const userInfo = useUserInfo()

    const Axios = axios.create({
        baseURL: getUrl(),
        timeout: 1000 * 10,
        headers: {
            'Content-Type': 'application/json',
            'lang': userSettings.defaultLang
        },
        responseType: 'json',
    })

    options = Object.assign(
        {
            CancelDuplicateRequest: true, // 是否开启取消重复请求, 默认为 true
            loading: false, // 是否开启loading层效果, 默认为false
            reductDataFormat: true, // 是否开启简洁的数据结构响应, 默认为true
            showErrorMessage: true, // 是否开启接口错误信息展示,默认为true
            showCodeMessage: true, // 是否开启code不为1时的信息提示, 默认为true
            showSuccessMessage: false, // 是否开启code为1时的信息提示, 默认为false
            anotherToken: '', // 当前请求使用另外的用户token
        },
        options
    )

    // 请求拦截
    Axios.interceptors.request.use(
        (config) => {
            removePending(config)
            options.CancelDuplicateRequest && addPending(config)
            // 创建loading实例
            if (options.loading) {
                loadingInstance.count++
                if (loadingInstance.count === 1) {
                    loadingInstance.target = ElLoading.service(loading)
                }
            }

            // 自动携带token
            if (config.headers) {
                const userToken = options.anotherToken || userInfo.getToken()
                if (userToken) config.headers['wk-user-token'] = userToken
            }

            return config
        },
        (error) => {
            return Promise.reject(error)
        }
    )

    // 响应拦截
    Axios.interceptors.response.use(
        (response) => {
            removePending(response.config)
            options.loading && closeLoading(options) // 关闭loading

            if (response.config.responseType == 'json') {
                if (response.data && response.data.code !== 0) {
                    if(response.data.code == 600098) {
                        if (!isRelogin.show) {
                            isRelogin.show = true;
                            ElMessageBox.confirm('登录失效，您可以继续留在该页面，或者重新登录', '系统提示', {
                                confirmButtonText: '重新登录',
                                cancelButtonText: '取消',
                                type: 'warning'
                            }
                            ).then(() => {
                                isRelogin.show = false
                                Local.remove(USER_INFO)
                                Local.remove(USER_SETTINGS)
                                Local.remove(USER_VIEWS)
                                Local.remove(TAGS_VIEW)
                                window.location.href = '/platform/login'
                            }).catch(() => {
                                isRelogin.show = false
                            })
                        }
                        return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
                    }
                    if(response.data.code == 500200) {
                        let note = ''
                        for (const key in response.data.data) {
                            let tempName = response.data.data[key].name
                            if (!tempName) {
                                tempName = key
                            }
                            note += tempName + ' : ' + response.data.data[key].msg + ' \r\n'
                        }
                        ElNotification({
                            type: 'error',
                            message: note,
                        })
                        return Promise.reject('参数错误')
                    }
                    if (options.showCodeMessage) {
                        ElNotification({
                            type: 'error',
                            message: response.data.msg,
                        })
                    }
                    // 自动跳转到路由name或path，仅限server端返回302的情况
                    if (response.data.code == 302) {
                        if (response.data.data.routeName) {
                            router.push({ name: response.data.data.routeName })
                        } else if (response.data.data.routePath) {
                            router.push({ path: response.data.data.routePath })
                        }
                    }
                    // code不等于0, 页面then内的具体逻辑就不执行了
                    return Promise.reject(response.data)
                } else if (options.showSuccessMessage && response.data && response.data.code == 0) {
                    ElNotification({
                        message: response.data.msg ? response.data.msg : i18n.global.t('axios.Operation successful'),
                        type: 'success',
                    })
                }
            }

            return options.reductDataFormat ? response.data : response
        },
        (error) => {
            error.config && removePending(error.config)
            options.loading && closeLoading(options) // 关闭loading
            options.showErrorMessage && httpErrorStatusHandle(error) // 处理错误状态码
            return Promise.reject(error) // 错误继续返回给到具体页面
        }
    )

    return Axios(axiosConfig)
}

export default createAxios

/**
 * 处理异常
 * @param {*} error
 */
function httpErrorStatusHandle(error: any) {
    // 处理被取消的请求
    if (axios.isCancel(error)) return console.error(i18n.global.t('axios.Automatic cancellation due to duplicate request:') + error.message)
    let message = ''
    if (error && error.response) {
        switch (error.response.status) {
            case 302:
                message = i18n.global.t('axios.Interface redirected!')
                break
            case 400:
                message = i18n.global.t('axios.Incorrect parameter!')
                break
            case 401:
                message = i18n.global.t('axios.You do not have permission to operate!')
                break
            case 403:
                message = i18n.global.t('axios.You do not have permission to operate!')
                break
            case 404:
                message = i18n.global.t('axios.Error requesting address:') + error.response.config.url
                break
            case 408:
                message = i18n.global.t('axios.Request timed out!')
                break
            case 409:
                message = i18n.global.t('axios.The same data already exists in the system!')
                break
            case 500:
                message = i18n.global.t('axios.Server internal error!')
                break
            case 501:
                message = i18n.global.t('axios.Service not implemented!')
                break
            case 502:
                message = i18n.global.t('axios.Gateway error!')
                break
            case 503:
                message = i18n.global.t('axios.Service unavailable!')
                break
            case 504:
                message = i18n.global.t('axios.The service is temporarily unavailable Please try again later!')
                break
            case 505:
                message = i18n.global.t('axios.HTTP version is not supported!')
                break
            default:
                message = i18n.global.t('axios.Abnormal problem, please contact the website administrator!')
                break
        }
    }
    if (error.message.includes('timeout')) message = i18n.global.t('axios.Network request timeout!')
    if (error.message.includes('Network'))
        message = window.navigator.onLine ? i18n.global.t('axios.Server exception!') : i18n.global.t('axios.You are disconnected!')

    ElNotification({
        type: 'error',
        message,
    })
}

/**
 * 关闭Loading层实例
 */
function closeLoading(options: Options) {
    if (options.loading && loadingInstance.count > 0) loadingInstance.count--
    if (loadingInstance.count === 0) {
        loadingInstance.target.close()
        loadingInstance.target = null
    }
}

/**
 * 储存每个请求的唯一cancel回调, 以此为标识
 */
function addPending(config: AxiosRequestConfig) {
    const pendingKey = getPendingKey(config)
    config.cancelToken =
        config.cancelToken ||
        new axios.CancelToken((cancel) => {
            if (!pendingMap.has(pendingKey)) {
                pendingMap.set(pendingKey, cancel)
            }
        })
}

/**
 * 删除重复的请求
 */
function removePending(config: AxiosRequestConfig) {
    const pendingKey = getPendingKey(config)
    if (pendingMap.has(pendingKey)) {
        const cancelToken = pendingMap.get(pendingKey)
        cancelToken(pendingKey)
        pendingMap.delete(pendingKey)
    }
}

/**
 * 生成每个请求的唯一key
 */
function getPendingKey(config: AxiosRequestConfig) {
    let { data } = config
    const { url, method, params, headers } = config
    if (typeof data === 'string') data = JSON.parse(data) // response里面返回的config.data是个字符串对象
    return [
        url,
        method,
        headers && headers['wk-user-token'] ? headers['wk-user-token'] : '',
        JSON.stringify(params),
        JSON.stringify(data),
    ].join('&')
}

export function requestPayload(method: Method, data: anyObj) {
    if (method == 'GET') {
        return {
            params: data,
        }
    } else if (method == 'POST') {
        return {
            data: data,
        }
    }
}

interface LoadingInstance {
    target: any
    count: number
}
interface Options {
    // 是否开启取消重复请求, 默认为 true
    CancelDuplicateRequest?: boolean
    // 是否开启loading层效果, 默认为false
    loading?: boolean
    // 是否开启简洁的数据结构响应, 默认为true
    reductDataFormat?: boolean
    // 是否开启接口错误信息展示,默认为true
    showErrorMessage?: boolean
    // 是否开启code不为0时的信息提示, 默认为true
    showCodeMessage?: boolean
    // 是否开启code为0时的信息提示, 默认为false
    showSuccessMessage?: boolean
    // 当前请求使用另外的用户token
    anotherToken?: string
}