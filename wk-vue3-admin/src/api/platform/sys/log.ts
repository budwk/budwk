export const API_SYS_LOG_LIST = '/platform/sys/log/list'
export const API_SYS_LOG_DATA = '/platform/sys/log/data'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_LOG_LIST,
        method: 'POST',
        data: data
    })
}


export function getData() {
    return request({
        url: API_SYS_LOG_DATA,
        method: 'GET'
    })
}