export const API_DEVICE_PRODUCT_INIT = '/device/admin/product/init'
export const API_DEVICE_PRODUCT_LIST = '/device/admin/product/list'
export const API_DEVICE_PRODUCT_GET = '/device/admin/product/get/'
export const API_DEVICE_PRODUCT_CREATE = '/device/admin/product/create'
export const API_DEVICE_PRODUCT_DELETE = '/device/admin/product/delete/'
export const API_DEVICE_PRODUCT_UPDATE = '/device/admin/product/update'

import request from '/@/utils/request'

export function getInit() {
    return request({
        url: API_DEVICE_PRODUCT_INIT,
        method: 'GET'
    })
}

export function getList(data: object = {}) {
    return request({
        url: API_DEVICE_PRODUCT_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_DEVICE_PRODUCT_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_DEVICE_PRODUCT_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_DEVICE_PRODUCT_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_DEVICE_PRODUCT_DELETE + id,
        method: 'DELETE'
    })
}
