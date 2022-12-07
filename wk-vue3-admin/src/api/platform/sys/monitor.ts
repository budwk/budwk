export const API_SYS_MONITOR_NACOS_SERVICES = '/platform/sys/monitor/nacos/services'
export const API_SYS_MONITOR_NACOS_SERVICE = '/platform/sys/monitor/nacos/service'
export const API_SYS_MONITOR_NACOS_DETAIL = '/platform/sys/monitor/nacos/detail'
export const API_SYS_MONITOR_REDIS_INFO = '/platform/sys/monitor/redis/info'
export const API_SYS_MONITOR_SERVER_INFO = '/platform/sys/monitor/server/info'

import request from '/@/utils/request'


export function getNacosServices(data: Object) {
    return request({
        url: API_SYS_MONITOR_NACOS_SERVICES,
        method: 'POST',
        data: data
    })
}

export function getNacosService(data: Object) {
    return request({
        url: API_SYS_MONITOR_NACOS_SERVICE,
        method: 'POST',
        data: data
    })
}


export function getNacosDetail(data: Object) {
    return request({
        url: API_SYS_MONITOR_NACOS_DETAIL,
        method: 'POST',
        data: data
    })
}

export function getRedisInfo() {
    return request({
        url: API_SYS_MONITOR_REDIS_INFO,
        method: 'GET'
    })
}

export function getServerInfo() {
    return request({
        url: API_SYS_MONITOR_SERVER_INFO,
        method: 'GET'
    })
}