export const API_WX_MSG_MASS_LIST = '/wechat/admin/msg/mass/list'
export const API_WX_MSG_MASS_NEWS_LIST = '/wechat/admin/msg/mass/news_list'
export const API_WX_MSG_MASS_NEWS_CREATE = '/wechat/admin/msg/mass/news_create'
export const API_WX_MSG_MASS_NEWS_DETAIL = '/wechat/admin/msg/mass/news_detail/'
export const API_WX_MSG_MASS_NEWS_DELETE = '/wechat/admin/msg/mass/news_delete/'
export const API_WX_MSG_MASS_PUSH = '/wechat/admin/msg/mass/push'
export const API_WX_USER_LIST = '/wechat/admin/user/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_MSG_MASS_LIST,
        method: 'POST',
        data: data
    })
}

export function getUserList(data: object) {
    return request({
        url: API_WX_USER_LIST,
        method: 'POST',
        data: data
    })
}

export function getNewsList(data: object) {
    return request({
        url: API_WX_MSG_MASS_NEWS_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_WX_MSG_MASS_NEWS_DETAIL + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_WX_MSG_MASS_NEWS_CREATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_WX_MSG_MASS_NEWS_DELETE + id,
        method: 'DELETE'
    })
}

export function doPush(data: object = {}) {
    return request({
        url: API_WX_MSG_MASS_PUSH,
        method: 'POST',
        data: data
    })
}