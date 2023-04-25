export const API_DEVICE_SUPPLIER_LIST = '/device/admin/supplier/list'
export const API_DEVICE_SUPPLIER_GET = '/device/admin/supplier/get/'
export const API_DEVICE_SUPPLIER_CREATE = '/device/admin/supplier/create'
export const API_DEVICE_SUPPLIER_DELETE = '/device/admin/supplier/delete/'
export const API_DEVICE_SUPPLIER_UPDATE = '/device/admin/supplier/update'
export const API_DEVICE_SUPPLIER_CODE_GET = '/device/admin/supplier/code/get/'
export const API_DEVICE_SUPPLIER_CODE_CREATE = '/device/admin/supplier/code/create'
export const API_DEVICE_SUPPLIER_CODE_DELETE = '/device/admin/supplier/code/delete/'
export const API_DEVICE_SUPPLIER_CODE_UPDATE = '/device/admin/supplier/code/update'

import request from '/@/utils/request'

export function getList(data: object = {}) {
    return request({
        url: API_DEVICE_SUPPLIER_LIST,
        method: 'POST',
        data: data
    })
}

export function getInfo(id: string) {
    return request({
        url: API_DEVICE_SUPPLIER_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_DEVICE_SUPPLIER_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_DEVICE_SUPPLIER_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_DEVICE_SUPPLIER_DELETE + id,
        method: 'DELETE'
    })
}

export function getCodeInfo(id: string) {
    return request({
        url: API_DEVICE_SUPPLIER_CODE_GET + id,
        method: 'GET'
    })
}


export function doCodeCreate(data: object = {}) {
    return request({
        url: API_DEVICE_SUPPLIER_CODE_CREATE,
        method: 'POST',
        data: data
    })
}

export function doCodeUpdate(data: object = {}) {
    return request({
        url: API_DEVICE_SUPPLIER_CODE_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doCodeDelete(id: string) {
    return request({
        url: API_DEVICE_SUPPLIER_CODE_DELETE + id,
        method: 'DELETE'
    })
}

