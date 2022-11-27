export const API_SYS_TASK_LIST = '/platform/sys/task/list'
export const API_SYS_TASK_CREATE = '/platform/sys/task/create'
export const API_SYS_TASK_GET = '/platform/sys/task/get/'
export const API_SYS_TASK_DELETE = '/platform/sys/task/delete/'
export const API_SYS_TASK_UPDATE = '/platform/sys/task/update'
export const API_SYS_TASK_DISABLED = '/platform/sys/task/disabled'
export const API_SYS_TASK_HISTORY = '/platform/sys/task/history'
export const API_SYS_TASK_DONOW = '/platform/sys/task/donow/'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_TASK_LIST,
        method: 'POST',
        data: data
    })
}

export function getHistoryList(data: object) {
    return request({
        url: API_SYS_TASK_HISTORY,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_SYS_TASK_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_TASK_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_TASK_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_TASK_DELETE + id,
        method: 'DELETE'
    })
}

export function doDisable(data: object = {}) {
    return request({
        url: API_SYS_TASK_DISABLED,
        method: 'POST',
        data: data
    })
}

export function doNow(id: string) {
    return request({
        url: API_SYS_TASK_DONOW + id,
        method: 'GET'
    })
}