export const API_PUB_USER_UNITLIST = '/platform/pub/user/unitlist'
export const API_PUB_USER_LIST = '/platform/pub/user/list'
export const API_PUB_USER_POST = '/platform/pub/user/post'


import request from '/@/utils/request'

export function getUnitList(data: object) {
    return request({
        url: API_PUB_USER_UNITLIST,
        method: 'GET',
        params: data
    })
}

export function getList(data: object) {
    return request({
        url: API_PUB_USER_LIST,
        method: 'POST',
        data: data
    })
}

export function getPostList() {
    return request({
        url: API_PUB_USER_POST,
        method: 'GET'
    })
}