export const API_WX_MENU_LIST = '/wechat/admin/conf/menu/list/'
// export const API_WX_MENU_CHILD = '/wechat/admin/conf/menu/child/'
export const API_WX_MENU_TREE = '/wechat/admin/conf/menu/tree/'
export const API_WX_MENU_CREATE = '/wechat/admin/conf/menu/create'
export const API_WX_MENU_DELETE = '/wechat/admin/conf/menu/delete/'
export const API_WX_MENU_GET = '/wechat/admin/conf/menu/get/'
export const API_WX_MENU_UPDATE = '/wechat/admin/conf/menu/update'
//export const API_WX_MENU_SORT_TREE = '/wechat/admin/conf/menu/get_sort_tree/'
export const API_WX_MENU_SORT = '/wechat/admin/conf/menu/sort/'
export const API_WX_MENU_PUSH = '/wechat/admin/conf/menu/push/'
// export const API_WX_MENU_LIST_CHANNEL = '/wechat/admin/conf/menu/list_channel'
// export const API_WX_MENU_LIST_ARTICLE = '/wechat/admin/conf/menu/list_article'
export const API_WX_MENU_LIST_KEYWORD = '/wechat/admin/conf/menu/list_keyword'


import request from '/@/utils/request'

export function getList(wxid: string) {
    return request({
        url: API_WX_MENU_LIST + wxid,
        method: 'GET'
    })
}

export function getTree(wxid: string,pid: string) {
    return request({
        url: API_WX_MENU_TREE + wxid,
        method: 'GET',
        params: { pid: pid}
    })
}

export function doSort(wxid: string,ids: string) {
    return request({
        url: API_WX_MENU_SORT + wxid,
        method: 'POST',
        data: { ids: ids}
    })
}

export function doPush(wxid: string) {
    return request({
        url: API_WX_MENU_PUSH + wxid,
        method: 'POST'
    })
}

export function getKeywordList(wxid: string) {
    return request({
        url: API_WX_MENU_LIST_KEYWORD,
        method: 'POST',
        data: { wxid: wxid }
    })
}

export function getInfo(id: string) {
    return request({
        url: API_WX_MENU_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_MENU_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_WX_MENU_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_MENU_DELETE + id,
        method: 'DELETE'
    })
}
