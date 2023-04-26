export const API_DEVICE_HANDLER_LIST = '/device/admin/handler/list'
export const API_DEVICE_HANDLER_GET = '/device/admin/handler/get/'
export const API_DEVICE_HANDLER_CREATE = '/device/admin/handler/create'
export const API_DEVICE_HANDLER_DELETE = '/device/admin/handler/delete/'
export const API_DEVICE_HANDLER_UPDATE = '/device/admin/handler/update'
export const API_DEVICE_HANDLER_ENABLED = '/device/admin/handler/enabled'

import request from '/@/utils/request'

export function getList(data: object = {}) {
    return request({
        url: API_DEVICE_HANDLER_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_DEVICE_HANDLER_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_DEVICE_HANDLER_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_DEVICE_HANDLER_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_DEVICE_HANDLER_DELETE + id,
        method: 'DELETE'
    })
}

export function doEnabled(id: string, enbaled: boolean) {
    return request({
        url: API_DEVICE_HANDLER_ENABLED,
        method: 'POST',
        data: {id: id, enabled: enbaled}
    })
}