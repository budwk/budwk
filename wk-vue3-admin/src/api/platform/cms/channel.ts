export const API_CMS_CHANNEL_LIST = '/cms/admin/channel/list'
export const API_CMS_CHANNEL_TREE = '/cms/admin/channel/tree/'
export const API_CMS_CHANNEL_DISABLED = '/cms/admin/channel/disabled'
export const API_CMS_CHANNEL_CREATE = '/cms/admin/channel/create'
export const API_CMS_CHANNEL_DELETE = '/cms/admin/channel/delete/'
export const API_CMS_CHANNEL_GET = '/cms/admin/channel/get/'
export const API_CMS_CHANNEL_UPDATE = '/cms/admin/channel/update'
export const API_CMS_CHANNEL_SORT = '/cms/admin/channel/sort/'
export const API_CMS_CHANNEL_LIST_SITE = '/cms/admin/channel/list_site'
export const API_CMS_CHANNEL_GET_TYPE = '/cms/admin/channel/get_type'


import request from '/@/utils/request'

export function getList(siteId: string) {
    return request({
        url: API_CMS_CHANNEL_LIST,
        method: 'GET',
        params: { siteId: siteId}
    })
}

export function getSiteList() {
    return request({
        url: API_CMS_CHANNEL_LIST_SITE,
        method: 'GET'
    })
}


export function getInfo(id: string) {
    return request({
        url: API_CMS_CHANNEL_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_CMS_CHANNEL_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_CMS_CHANNEL_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_CMS_CHANNEL_DELETE + id,
        method: 'DELETE'
    })
}

export function doSort(ids: string, siteId: string) {
    return request({
        url: API_CMS_CHANNEL_SORT + siteId,
        method: 'POST',
        data: { ids: ids}
    })
}