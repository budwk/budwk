export const API_DEVICE_DEVTYPE_INIT = '/device/admin/devtype/init'
export const API_DEVICE_DEVTYPE_SUBTYPE = '/device/admin/devtype/subtype/'
export const API_DEVICE_DEVTYPE_LIST = '/device/admin/devtype/list'
export const API_DEVICE_DEVTYPE_GET = '/device/admin/devtype/get/'
export const API_DEVICE_DEVTYPE_CREATE = '/device/admin/devtype/create'
export const API_DEVICE_DEVTYPE_DELETE = '/device/admin/devtype/delete/'
export const API_DEVICE_DEVTYPE_UPDATE = '/device/admin/devtype/update'

import request from '/@/utils/request'

export function getInit() {
    return request({
        url: API_DEVICE_DEVTYPE_INIT,
        method: 'GET'
    })
}

export function getSubType(pid: string) {
    return request({
        url: API_DEVICE_DEVTYPE_SUBTYPE + pid,
        method: 'GET'
    })
}

export function getList(data: object = {}) {
    return request({
        url: API_DEVICE_DEVTYPE_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_DEVICE_DEVTYPE_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_DEVICE_DEVTYPE_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_DEVICE_DEVTYPE_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_DEVICE_DEVTYPE_DELETE + id,
        method: 'DELETE'
    })
}
