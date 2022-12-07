
export const API_HOME_MSG_MY_LIST = '/platform/home/msg/my_msg_list'
export const API_HOME_MSG_GET = '/platform/home/msg/get/'
export const API_HOME_MSG_READ_ONE = '/platform/home/msg/status/read_one/'
export const API_HOME_MSG_READ_ALL = '/platform/home/msg/status/read_all'
export const API_HOME_MSG_READ_MORE = '/platform/home/msg/status/read_more'
export const API_HOME_MSG_DATA = '/platform/home/msg/data'
export const API_HOME_MSG_WS = '/platform/home/msg/wsmsg'

import request from '/@/utils/request'

export function getWsMsg() {
    return request({
        url: API_HOME_MSG_WS,
        method: 'GET'
    })
}

export function getList(data: object) {
    return request({
        url: API_HOME_MSG_MY_LIST,
        method: 'POST',
        data: data
    })
}

export function getInfo(id: string) {
    return request({
        url: API_HOME_MSG_GET + id,
        method: 'POST'
    })
}

export function getData() {
    return request({
        url: API_HOME_MSG_DATA,
        method: 'GET'
    })
}

export function doReadAll() {
    return request({
        url: API_HOME_MSG_READ_ALL,
        method: 'POST'
    })
}

export function doReadMore(ids: string) {
    return request({
        url: API_HOME_MSG_READ_MORE,
        method: 'POST',
        data: {ids: ids}
    })
}

export function doReadOne(id: string) {
    return request({
        url: API_HOME_MSG_READ_ONE + '/' + id,
        method: 'POST'
    })
}