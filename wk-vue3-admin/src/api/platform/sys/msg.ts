export const API_SYS_MSG_LIST = '/platform/sys/msg/list'
export const API_SYS_MSG_CREATE = '/platform/sys/msg/create'
export const API_SYS_MSG_DELETE = '/platform/sys/msg/delete/'
export const API_SYS_MSG_GET_USER_VIEW_LIST = '/platform/sys/msg/get_user_view_list'
export const API_SYS_MSG_SELECT_USER_LIST = '/platform/sys/msg/select_user_list'
export const API_SYS_MSG_DATA = '/platform/sys/msg/data'


import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_MSG_LIST,
        method: 'POST',
        data: data
    })
}

export function getViewUserList(data: object) {
    return request({
        url: API_SYS_MSG_GET_USER_VIEW_LIST,
        method: 'POST',
        data: data
    })
}

export function getData() {
    return request({
        url: API_SYS_MSG_DATA,
        method: 'GET'
    })
}

export function getInfo(id: string) {
    return request({
        url: API_SYS_MSG_DATA + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_MSG_CREATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_MSG_DELETE + id,
        method: 'DELETE'
    })
}