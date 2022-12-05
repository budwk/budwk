export const API_SYS_SECURITY_GET = '/platform/sys/security/get'
export const API_SYS_SECURITY_SAVE = '/platform/sys/security/save'

import request from '/@/utils/request'

export function getInfo() {
    return request({
        url: API_SYS_SECURITY_GET,
        method: 'GET'
    })
}

export function doSave(data: object = {}) {
    return request({
        url: API_SYS_SECURITY_SAVE,
        method: 'POST',
        data: data
    })
}