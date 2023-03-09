export const API_WX_CONF_MINA_GET = '/wechat/admin/conf/mina/get/'
export const API_WX_CONF_MINA_DELETE = '/wechat/admin/conf/mina/delete/'
export const API_WX_CONF_MINA_CREATE = '/wechat/admin/conf/mina/create'
export const API_WX_CONF_MINA_UPDATE = '/wechat/admin/conf/mina/update'
export const API_WX_CONF_MINA_LIST = '/wechat/admin/conf/mina/list'
export const API_WX_CONFIG_PAY_QUERY = '/wechat/admin/conf/pay/query'


import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_CONF_MINA_LIST,
        method: 'POST',
        data: data
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
        url: API_WX_CONF_MINA_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_CONF_MINA_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_CONF_MINA_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_CONF_MINA_DELETE + id,
        method: 'DELETE'
    })
}