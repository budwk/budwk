export const API_SYS_POST_LIST = '/platform/sys/post/list'
export const API_SYS_POST_GET = '/platform/sys/post/get/'
export const API_SYS_POST_CREATE = '/platform/sys/post/create'
export const API_SYS_POST_DELETE = '/platform/sys/post/delete/'
export const API_SYS_POST_UPDATE = '/platform/sys/post/update'
export const API_SYS_POST_LOCATION = '/platform/sys/post/location'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_POST_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_SYS_POST_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_POST_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_POST_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_POST_DELETE + id,
        method: 'DELETE'
    })
}

export function doLocation(data: object = {}) {
    return request({
        url: API_SYS_POST_LOCATION,
        method: 'POST',
        data: data
    })
}