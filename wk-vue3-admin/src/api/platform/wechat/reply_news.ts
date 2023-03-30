export const API_WX_REPLY_NEWS_GET = '/wechat/admin/reply/news/get/'
export const API_WX_REPLY_NEWS_DELETE = '/wechat/admin/reply/news/delete/'
export const API_WX_REPLY_NEWS_DELETE_MORE = '/wechat/admin/reply/news/delete_more'
export const API_WX_REPLY_NEWS_CREATE = '/wechat/admin/reply/news/create'
export const API_WX_REPLY_NEWS_UPDATE = '/wechat/admin/reply/news/update'
export const API_WX_REPLY_NEWS_LIST = '/wechat/admin/reply/news/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_REPLY_NEWS_LIST,
        method: 'POST',
        data: data
    })
}

export function getInfo(id: string) {
    return request({
        url: API_WX_REPLY_NEWS_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_REPLY_NEWS_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_REPLY_NEWS_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_REPLY_NEWS_DELETE + id,
        method: 'DELETE'
    })
}

export function doDeleteMore(ids: string, titles: string) {
    return request({
        url: API_WX_REPLY_NEWS_DELETE_MORE,
        method: 'POST',
        data: { ids: ids, titles: titles }
    })
}