export const API_SYS_PARAM_LIST = '/platform/sys/param/list'
export const API_SYS_PARAM_GET = '/platform/sys/param/get/'
export const API_SYS_PARAM_CREATE = '/platform/sys/param/create'
export const API_SYS_PARAM_DELETE = '/platform/sys/param/delete/'
export const API_SYS_PARAM_UPDATE = '/platform/sys/param/update'
export const API_SYS_PARAM_DATA = '/platform/sys/param/data'


import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_PARAM_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_SYS_PARAM_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_PARAM_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_PARAM_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_PARAM_DELETE + id,
        method: 'DELETE'
    })
}

export function getData() {
    return request({
        url: API_SYS_PARAM_DATA,
        method: 'GET'
    })
}