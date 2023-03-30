export const API_WX_REPLY_IMG_GET = '/wechat/admin/reply/img/get/'
export const API_WX_REPLY_IMG_DELETE = '/wechat/admin/reply/img/delete/'
export const API_WX_REPLY_IMG_DELETE_MORE = '/wechat/admin/reply/img/delete_more'
export const API_WX_REPLY_IMG_CREATE = '/wechat/admin/reply/img/create'
export const API_WX_REPLY_IMG_UPDATE = '/wechat/admin/reply/img/update'
export const API_WX_REPLY_IMG_LIST = '/wechat/admin/reply/img/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_REPLY_IMG_LIST,
        method: 'POST',
        data: data
    })
}

export function getInfo(id: string) {
    return request({
        url: API_WX_REPLY_IMG_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_REPLY_IMG_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_REPLY_IMG_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_REPLY_IMG_DELETE + id,
        method: 'DELETE'
    })
}

export function doDeleteMore(ids: string, picurls: string) {
    return request({
        url: API_WX_REPLY_IMG_DELETE_MORE,
        method: 'POST',
        data: { ids: ids, picurls: picurls }
    })
}