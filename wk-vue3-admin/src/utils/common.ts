import { nextTick } from 'vue'
import type { App } from 'vue'
import * as elIcons from '@element-plus/icons-vue'
import router from '/@/router/index'
import Icon from '/@/components/icon/index.vue'
import { ElForm, ElNotification } from 'element-plus'
import { usePlatformInfo } from '../stores/platformInfo'
import { useUserSettings } from '../stores/userSettings'
import { i18n } from '../lang'
import { getUrl } from './request'
import { env } from 'echarts'
import { isNavigationFailure, NavigationFailureType, RouteLocationRaw, RouteRecordRaw } from 'vue-router'

/**
 * 从数组中查找某个对象属性值
 * @param {any} obj 数组对象
 * @param {string} value 匹配值
 * @param {string} attribute 返回的属性名
 * @param {string} name 匹配的属性名(默认为id)
 * @returns {string}
 */
export const findOneValue = (obj: any, value: string, attribute: string, name = 'id',) => {
    if(obj && obj.length > 0) {
        const index = obj.findIndex((o: any)=>{
            return o[name] === value
        })
        if(index>-1){
            return obj[index][attribute]
        }
    }
    return ''
}

/**
 * 获取第一个菜单
 */
export const getFirstRoute = (routes: RouteRecordRaw[]): any => {
    for (const key in routes) {
        return routes[key]
    }
}

/**
 * 导航失败有错误消息的路由push
 * @param to — 导航位置，同 router.push
 */
export const routePush = async (name: string) => {
    try {
        const failure = await router.push({name:name})
        if (isNavigationFailure(failure, NavigationFailureType.aborted)) {
            ElNotification({
                message: i18n.global.t('utils.Navigation failed, navigation guard intercepted!'),
                type: 'error',
            })
        } else if (isNavigationFailure(failure, NavigationFailureType.duplicated)) {
            ElNotification({
                message: i18n.global.t('utils.Navigation failed, it is at the navigation target position!'),
                type: 'warning',
            })
        }
    } catch (error) {
        ElNotification({
            message: i18n.global.t('utils.Navigation failed, invalid route!'),
            type: 'error',
        })
        console.error(error)
    }
}

export function registerIcons(app: App) {
    /*
     * 全局注册 icon
     * 使用方式1: <icon name="el-icon-Right" size="size" color="color" />
     * 使用方式2: <right style="width: 1em; height: 1em;" />
     * 详见<待完善>
     */
    app.component('Icon', Icon)

    /*
     * 全局注册element Plus的icon
     */
    const icons = elIcons as any
    for (const i in icons) {
        app.component(`${icons[i].name}`, icons[i])
        app.component(`el-icon-${icons[i].name}`, icons[i])
    }
}

/* 加载网络css文件 */
export function loadCss(url: string): void {
    const link = document.createElement('link')
    link.rel = 'stylesheet'
    link.href = url
    link.crossOrigin = 'anonymous'
    document.getElementsByTagName('head')[0].appendChild(link)
}

/* 加载网络js文件 */
export function loadJs(url: string): void {
    const link = document.createElement('script')
    link.src = url
    document.body.appendChild(link)
}

/**
 * 设置浏览器标题
 */
export function setTitleFromRoute() {
    const userSettings = useUserSettings()
    const platformInfo = usePlatformInfo()
    nextTick(() => {
        let webTitle = platformInfo.AppName
        if(userSettings.dynamicTitle && router.currentRoute.value.meta && router.currentRoute.value.meta.title){
            if ((router.currentRoute.value.meta.title as string).indexOf('pagesTitle.') === -1) {
                webTitle = router.currentRoute.value.meta.title as string + "｜" +  platformInfo.AppName 
            } else {
                webTitle = i18n.global.t(router.currentRoute.value.meta.title as string) + "｜" + platformInfo.AppName  
            }
        }
        document.title = `${webTitle}`
    })
}

export function setTitle(title: string) {
    document.title = `${title}`
}


/**
 * 判断url是否是http或https 
 * @param {string} path
 * @returns {Boolean}
 */
export function isHttp(url: string) {
    return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1
}
  
/**
   * 判断path是否为外链
   * @param {string} path
   * @returns {Boolean}
   */
export function isExternal(path: string) {
    return /^(https?:|mailto:|tel:)/.test(path)
}
  

/**
   * 返回项目路径
   * @param {string} path
   * @returns {String}
   */
export function getNormalPath(path:string): string{
    if(!path || path == 'undefined' || path.length === 0) {
        return path
    }
    const res = path.replace('//', '/')
    if (res[res.length - 1] === '/') {
        return res.slice(0, res.length - 1)
    }
    return res;
}

/**
 * 防抖
 * @param fn 执行函数
 * @param ms 间隔毫秒数
 */
export const debounce = (fn: Function, ms: number) => {
    return (...args: any[]) => {
        if (window.lazy) {
            clearTimeout(window.lazy)
        }
        window.lazy = setTimeout(() => {
            fn(...args)
        }, ms)
    }
}

/**
 * 根据pk字段的值从数组中获取key
 * @param arr
 * @param pk
 * @param value
 */
export const getArrayKey = (arr: any, pk: string, value: string): any => {
    for (const key in arr) {
        if (arr[key][pk] == value) {
            return key
        }
    }
    return false
}

/**
 * 表单重置
 * @param formEl
 */
export const onResetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    if (!formEl) return
    formEl.resetFields()
}

/**
 * 将数据构建为ElTree的data {label:'', children: []}
 * @param data
 */
export const buildJsonToElTreeData = (data: any): ElTreeData[] => {
    if (typeof data == 'object') {
        const childrens = []
        for (const key in data) {
            childrens.push({
                label: key + ': ' + data[key],
                children: buildJsonToElTreeData(data[key]),
            })
        }
        return childrens
    } else {
        return []
    }
}

/**
 * 是否在后台应用内
 */
export const isPlatformApp = () => {
    if (/^\/platform/.test(router.currentRoute.value.fullPath) || window.location.hash.indexOf('#/platform') === 0) {
        return true
    }
    return false
}

/**
 * 从一个文件路径中获取文件名
 * @param path 文件路径
 */
export const getFileNameFromPath = (path: string) => {
    const paths = path.split('/')
    return paths[paths.length - 1]
}

/**
 * 获取资源完整地址
 * @param relativeUrl 资源相对地址
 * @param domain 指定域名
 */
export const fullUrl = (relativeUrl: string, domain = '') => {
    const platformInfo = usePlatformInfo()
    if (!domain) {
        domain = platformInfo.AppDomain ? platformInfo.AppDomain : getUrl()
    }
    if (!relativeUrl) return domain

    const regUrl = new RegExp(/^http(s)?:\/\//)
    const regexImg = new RegExp(/^((?:[a-z]+:)?\/\/|data:image\/)(.*)/i)
    if (!domain || regUrl.test(relativeUrl) || regexImg.test(relativeUrl)) {
        return relativeUrl
    }
    return domain + relativeUrl
}


export const arrayFullUrl = (relativeUrls: string | string[], domain = '') => {
    if (typeof relativeUrls === 'string') {
        relativeUrls = relativeUrls == '' ? [] : relativeUrls.split(',')
    }
    for (const key in relativeUrls) {
        relativeUrls[key] = fullUrl(relativeUrls[key], domain)
    }
    return relativeUrls
}



/*
 * 格式化时间戳
 */
export const formatTime = (dateTime: string | number | null = null, fmt = 'yyyy-mm-dd hh:MM:ss') => {
    if (dateTime == 'none') return i18n.global.t('none')
    if (!dateTime) return i18n.global.t('none')
    if (dateTime.toString().length === 10) {
        dateTime = +dateTime * 1000
    }

    const date = new Date(dateTime)
    let ret
    const opt: anyObj = {
        'y+': date.getFullYear().toString(), // 年
        'm+': (date.getMonth() + 1).toString(), // 月
        'd+': date.getDate().toString(), // 日
        'h+': date.getHours().toString(), // 时
        'M+': date.getMinutes().toString(), // 分
        's+': date.getSeconds().toString(), // 秒
    }
    for (const k in opt) {
        ret = new RegExp('(' + k + ')').exec(fmt)
        if (ret) {
            fmt = fmt.replace(ret[1], ret[1].length == 1 ? opt[k] : padStart(opt[k], ret[1].length, '0'))
        }
    }
    return fmt
}

/*
 * 格式化时间戳
 */
export const formatDate = (dateTime: string | number | null = null, fmt = 'yyyy-mm-dd') => {
    if (dateTime == 'none') return i18n.global.t('none')
    if (!dateTime) dateTime = Number(new Date())
    if (dateTime.toString().length === 10) {
        dateTime = +dateTime * 1000
    }

    const date = new Date(dateTime)
    let ret
    const opt: anyObj = {
        'y+': date.getFullYear().toString(), // 年
        'm+': (date.getMonth() + 1).toString(), // 月
        'd+': date.getDate().toString(), // 日
        'h+': date.getHours().toString(), // 时
        'M+': date.getMinutes().toString(), // 分
        's+': date.getSeconds().toString(), // 秒
    }
    for (const k in opt) {
        ret = new RegExp('(' + k + ')').exec(fmt)
        if (ret) {
            fmt = fmt.replace(ret[1], ret[1].length == 1 ? opt[k] : padStart(opt[k], ret[1].length, '0'))
        }
    }
    return fmt
}

/*
 * 字符串补位
 */
export const padStart = (str: string, maxLength: number, fillString = ' ') => {
    if (str.length >= maxLength) return str

    const fillLength = maxLength - str.length
    let times = Math.ceil(fillLength / fillString.length)
    while ((times >>= 1)) {
        fillString += fillString
        if (times === 1) {
            fillString += fillString
        }
    }
    return fillString.slice(0, fillLength) + str
}

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} lazy 是否lazy加载(非lazy加载要排除hasChildren字段)
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export const handleTree = (data: any, lazy=false, id='id', parentId='parentId', children='children') => {
    const config = {
        lazy: lazy,
        id: id,
        parentId: parentId,
        childrenList: children
    }
  
    const childrenListMap: any = {}
    const nodeIds:any = {}
    const tree = []
  
    for (const d of data) {
        const parentId = d[config.parentId];
        if (childrenListMap[parentId] == null) {
            childrenListMap[parentId] = []
        }
        if(!config.lazy){
            d.hasChildren = undefined
        }
        nodeIds[d[config.id]] = d;
        childrenListMap[parentId].push(d)
    }
  
    for (const d of data) {
        const parentId = d[config.parentId]
        if (nodeIds[parentId] == null) {
            if(!config.lazy){
                d.hasChildren = undefined
            }
            tree.push(d)
        }
    }
  
    for (const t of tree) {
        adaptToChildrenList(t);
    }
  
    function adaptToChildrenList(o: any) {
        if (childrenListMap[o[config.id]] !== null) {
            o[config.childrenList] = childrenListMap[o[config.id]]
        }
        if (o[config.childrenList]) {
            for (const c of o[config.childrenList]) {
                adaptToChildrenList(c)
            }
        }
    }
    return tree;
}

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} children 孩子节点字段 默认 'children'
 */
export const handleTreeWithPath = (data: any, children='children') => {
    const config = {
        childrenList: children
    }
  
    const childrenListMap: any = {}
    const nodeIds:any = {}
    const tree = []
  
    for (const d of data) {
        const parentId = getParentId(d['path'])
        if (childrenListMap[parentId] == null) {
            childrenListMap[parentId] = []
        }
        nodeIds[d['path']] = d
        childrenListMap[parentId].push(d)
    }
  
    for (const d of data) {
        const parentId = getParentId(d['path'])
        if (nodeIds[parentId] == null) {
            tree.push(d)
        }
    }
  
    for (const t of tree) {
        adaptToChildrenList(t)
    }
  
    function getParentId(path: string) {
        if( path==null || path==undefined) return ''
        return path.substring(0,path.length -4)
    }

    function adaptToChildrenList(o: any) {
        if (childrenListMap[o['path']] !== null) {
            o[config.childrenList] = childrenListMap[o['path']]
        }
        if (o[config.childrenList]) {
            for (const c of o[config.childrenList]) {
                adaptToChildrenList(c)
            }
        }
    }
    return tree;
}


export const blobValidate = (data:any) => {
    try {
        const text = data.text();
        JSON.parse(text);
        return false;
    } catch (error) {
        return true;
    }
}

/**
 * 添加日期范围 YYYY-MM-DD 00:00:00.000 时间戳至 YYYY-MM-DD 23:59:59.999
 * @param {*} params 传参对象
 * @param {*} dateRange 日期范围（时间戳，结束时间会 -1ms）
 * @param {*} propName 字段名后缀
 */
export const addDateRange = (params: any, dateRange: any, propName = undefined) => {
    if(typeof (params) === 'undefined' || typeof (params) !== 'object'){
        return
    }
    dateRange = Array.isArray(dateRange) ? dateRange : []
    if (typeof (propName) === 'undefined') {
        params['beginTime'] = dateRange[0]
        params['endTime'] = dateRange[1] + (3600 * 24 * 1000 ) - 1
    } else {
        params['begin' + propName] = dateRange[0]
        params['end' + propName] = dateRange[1]  + (3600 * 24 * 1000 ) - 1
    }
}

// 隐藏手机号中间数字
export const hiddenMobile = (mobile: string) => {
    if(mobile && isMobile(mobile) ) {
        return mobile.substring(0,3) + "****" + mobile.substring(7,11)
    }
    return mobile
}

// 是否手机号
export const isMobile = (mobile: string) => {
    return /^(1[3-9])\d{9}$/.test(mobile)
}