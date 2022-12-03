export const API_SYS_KEY_LIST = '/platform/sys/key/list'
export const API_SYS_KEY_CREATE = '/platform/sys/key/create'
export const API_SYS_KEY_DELETE = '/platform/sys/key/delete/'
export const API_SYS_KEY_DISABLED = '/platform/sys/key/disabled'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_KEY_LIST,
        method: 'POST',
        data: data
    })
}


export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_KEY_CREATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_KEY_DELETE + id,
        method: 'DELETE'
    })
}

export function doDisable(data: object = {}) {
    return request({
        url: API_SYS_KEY_DISABLED,
        method: 'POST',
        data: data
    })
}