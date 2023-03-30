export const API_WX_REPLY_CONF_GET = '/wechat/admin/reply/conf/get/'
export const API_WX_REPLY_CONF_DELETE = '/wechat/admin/reply/conf/delete/'
export const API_WX_REPLY_CONF_DELETE_MORE = '/wechat/admin/reply/conf/delete_more'
export const API_WX_REPLY_CONF_CREATE = '/wechat/admin/reply/conf/create'
export const API_WX_REPLY_CONF_UPDATE = '/wechat/admin/reply/conf/update'
export const API_WX_REPLY_CONF_LIST = '/wechat/admin/reply/conf/list'
export const API_WX_REPLY_CONF_LIST_CONTENT = '/wechat/admin/reply/conf/list_content'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_REPLY_CONF_LIST,
        method: 'POST',
        data: data
    })
}

export function getContentList(wxid: string, msgType: string) {
    return request({
        url: API_WX_REPLY_CONF_LIST_CONTENT,
        method: 'POST',
        data: { wxid: wxid, msgType: msgType}
    })
}

export function getInfo(id: string) {
    return request({
        url: API_WX_REPLY_CONF_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_REPLY_CONF_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_REPLY_CONF_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_REPLY_CONF_DELETE + id,
        method: 'DELETE'
    })
}

export function doDeleteMore(ids: string, types: string) {
    return request({
        url: API_WX_REPLY_CONF_DELETE_MORE,
        method: 'POST',
        data: { ids: ids, types: types }
    })
}