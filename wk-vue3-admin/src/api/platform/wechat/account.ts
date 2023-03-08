export const API_WX_CONFIG_GET = '/wechat/admin/conf/account/get/'
export const API_WX_CONFIG_DELETE = '/wechat/admin/conf/account/delete/'
export const API_WX_CONFIG_CREATE = '/wechat/admin/conf/account/create'
export const API_WX_CONFIG_UPDATE = '/wechat/admin/conf/account/update'
export const API_WX_CONFIG_LIST = '/wechat/admin/conf/account/list'
export const API_WX_CONFIG_LIST_ACCOUNT = '/wechat/admin/conf/account/list_account'
export const API_WX_CONFIG_PAY_QUERY = '/wechat/admin/conf/pay/query'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_CONFIG_LIST,
        method: 'POST',
        data: data
    })
}

export function getAccountList() {
    return request({
        url: API_WX_CONFIG_LIST_ACCOUNT,
        method: 'GET'
    })
}

export function getPayList() {
    return request({
        url: API_WX_CONFIG_PAY_QUERY,
        method: 'GET'
    })
}

export function getInfo(id: string) {
    return request({
        url: API_WX_CONFIG_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_CONFIG_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_CONFIG_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_CONFIG_DELETE + id,
        method: 'DELETE'
    })
}