export const API_CMS_SITE_GET = '/cms/admin/site/get/'
export const API_CMS_SITE_DELETE = '/cms/admin/site/delete/'
export const API_CMS_SITE_LIST = '/cms/admin/site/list'
export const API_CMS_SITE_CREATE = '/cms/admin/site/create'
export const API_CMS_SITE_UPDATE = '/cms/admin/site/update'


import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_CMS_SITE_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_CMS_SITE_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_CMS_SITE_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_CMS_SITE_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_CMS_SITE_DELETE + id,
        method: 'DELETE'
    })
}