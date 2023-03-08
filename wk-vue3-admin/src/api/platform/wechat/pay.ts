export const API_WX_CONFIG_PAY_GET = '/wechat/admin/conf/pay/get/'
export const API_WX_CONFIG_PAY_DELETE = '/wechat/admin/conf/pay/delete/'
export const API_WX_CONFIG_PAY_CREATE = '/wechat/admin/conf/pay/create'
export const API_WX_CONFIG_PAY_UPDATE = '/wechat/admin/conf/pay/update'
export const API_WX_CONFIG_PAY_LIST = '/wechat/admin/conf/pay/list'
export const API_WX_CONFIG_PAYCERT_LIST = '/wechat/admin/conf/pay/cert/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_CONFIG_PAY_LIST,
        method: 'POST',
        data: data
    })
}

export function getCertList(data: object) {
    return request({
        url: API_WX_CONFIG_PAYCERT_LIST,
        method: 'POST',
        data: data
    })
}

export function getInfo(id: string) {
    return request({
        url: API_WX_CONFIG_PAY_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_CONFIG_PAY_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_CONFIG_PAY_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_CONFIG_PAY_DELETE + id,
        method: 'DELETE'
    })
}