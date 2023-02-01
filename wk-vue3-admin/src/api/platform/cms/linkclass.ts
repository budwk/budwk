export const API_CMS_LINKCLASS_GET = '/cms/admin/links/class/get/'
export const API_CMS_LINKCLASS_DELETE = '/cms/admin/links/class/delete/'
export const API_CMS_LINKCLASS_CREATE = '/cms/admin/links/class/create'
export const API_CMS_LINKCLASS_UPDATE = '/cms/admin/links/class/update'
export const API_CMS_LINKCLASS_LIST = '/cms/admin/links/class/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_CMS_LINKCLASS_LIST,
        method: 'POST',
        data: data
    })
}

export function getInfo(id: string) {
    return request({
        url: API_CMS_LINKCLASS_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_CMS_LINKCLASS_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_CMS_LINKCLASS_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_CMS_LINKCLASS_DELETE + id,
        method: 'DELETE'
    })
}