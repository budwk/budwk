export const API_WX_REPLY_TXT_GET = '/wechat/admin/reply/txt/get/'
export const API_WX_REPLY_TXT_DELETE = '/wechat/admin/reply/txt/delete/'
export const API_WX_REPLY_TXT_DELETE_MORE = '/wechat/admin/reply/txt/delete_more'
export const API_WX_REPLY_TXT_CREATE = '/wechat/admin/reply/txt/create'
export const API_WX_REPLY_TXT_UPDATE = '/wechat/admin/reply/txt/update'
export const API_WX_REPLY_TXT_LIST = '/wechat/admin/reply/txt/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_REPLY_TXT_LIST,
        method: 'POST',
        data: data
    })
}

export function getInfo(id: string) {
    return request({
        url: API_WX_REPLY_TXT_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_REPLY_TXT_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_REPLY_TXT_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_REPLY_TXT_DELETE + id,
        method: 'DELETE'
    })
}

export function doDeleteMore(ids: string, titles: string) {
    return request({
        url: API_WX_REPLY_TXT_DELETE_MORE,
        method: 'POST',
        data: { ids: ids, titles: titles }
    })
}