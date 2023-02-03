export const API_CMS_LINK_GET = '/cms/admin/links/link/get/'
export const API_CMS_LINK_DELETE = '/cms/admin/links/link/delete/'
export const API_CMS_LINK_DELETE_MORE = '/cms/admin/links/link/delete_more'
export const API_CMS_LINK_CREATE = '/cms/admin/links/link/create'
export const API_CMS_LINK_UPDATE = '/cms/admin/links/link/update'
export const API_CMS_LINK_LIST = '/cms/admin/links/link/list'
export const API_CMS_LINK_LIST_CLASS = '/cms/admin/links/link/list_class'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_CMS_LINK_LIST,
        method: 'POST',
        data: data
    })
}

export function getClassList() {
    return request({
        url: API_CMS_LINK_LIST_CLASS,
        method: 'GET'
    })
}

export function getInfo(id: string) {
    return request({
        url: API_CMS_LINK_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_CMS_LINK_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_CMS_LINK_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_CMS_LINK_DELETE + id,
        method: 'DELETE'
    })
}

export function doDeleteMore(ids: string, names: string) {
    return request({
        url: API_CMS_LINK_DELETE_MORE,
        method: 'POST',
        data: {ids: ids , names: names}
    })
}