export const API_SYS_APP_LIST = '/platform/sys/app/list'
export const API_SYS_APP_GET = '/platform/sys/app/get/'
export const API_SYS_APP_CREATE = '/platform/sys/app/create'
export const API_SYS_APP_DELETE = '/platform/sys/app/delete/'
export const API_SYS_APP_UPDATE = '/platform/sys/app/update'
export const API_SYS_APP_DISABLED = '/platform/sys/app/disabled'
export const API_SYS_APP_LOCATION = '/platform/sys/app/location'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_APP_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_SYS_APP_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_APP_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_APP_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_APP_DELETE + id,
        method: 'DELETE'
    })
}

export function doDisable(data: object = {}) {
    return request({
        url: API_SYS_APP_DISABLED,
        method: 'POST',
        data: data
    })
}

export function doLocation(data: object = {}) {
    return request({
        url: API_SYS_APP_LOCATION,
        method: 'POST',
        data: data
    })
}