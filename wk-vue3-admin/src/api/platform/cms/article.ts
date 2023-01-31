export const API_CMS_ARTICLE_GET = '/cms/admin/article/get/'
export const API_CMS_ARTICLE_DELETE = '/cms/admin/article/delete/'
export const API_CMS_ARTICLE_DELETE_MORE = '/cms/admin/article/delete_more'
export const API_CMS_ARTICLE_CREATE = '/cms/admin/article/create'
export const API_CMS_ARTICLE_UPDATE = '/cms/admin/article/update'
export const API_CMS_ARTICLE_LIST = '/cms/admin/article/list'
export const API_CMS_ARTICLE_GET_CHANNEL_LIST = '/cms/admin/article/get_channel_list/'
export const API_CMS_ARTICLE_DISABLED = '/cms/admin/article/disabled'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_CMS_ARTICLE_LIST,
        method: 'POST',
        data: data
    })
}

export function getChannelList(siteId: string) {
    return request({
        url: API_CMS_ARTICLE_GET_CHANNEL_LIST + siteId,
        method: 'GET'
    })
}

export function getInfo(id: string) {
    return request({
        url: API_CMS_ARTICLE_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_CMS_ARTICLE_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_CMS_ARTICLE_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_CMS_ARTICLE_DELETE + id,
        method: 'DELETE'
    })
}

export function doDeleteMore(ids: string, titles: string) {
    return request({
        url: API_CMS_ARTICLE_DELETE_MORE,
        method: 'POST',
        data: {ids: ids , titles: titles}
    })
}