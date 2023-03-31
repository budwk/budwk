export const API_WX_TPL_ID_DELETE = '/wechat/admin/tpl/id/delete/'
export const API_WX_TPL_ID_DELETE_MORE = '/wechat/admin/tpl/id/delete_more'
export const API_WX_TPL_ID_CREATE = '/wechat/admin/tpl/id/create'
export const API_WX_TPL_ID_LIST = '/wechat/admin/tpl/id/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_TPL_ID_LIST,
        method: 'POST',
        data: data
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_TPL_ID_CREATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_TPL_ID_DELETE + id,
        method: 'DELETE'
    })
}

export function doDeleteMore(ids: string, template_ids: string) {
    return request({
        url: API_WX_TPL_ID_DELETE_MORE,
        method: 'POST',
        data: { ids: ids, template_ids: template_ids }
    })
}