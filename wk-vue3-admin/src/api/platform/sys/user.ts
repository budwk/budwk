export const API_SYS_USER_UNITLIST = '/platform/sys/user/unitlist'
export const API_SYS_USER_LIST = '/platform/sys/user/list'
export const API_SYS_USER_POST = '/platform/sys/user/post'
export const API_SYS_USER_COUNT = '/platform/sys/user/count/'
export const API_SYS_USER_NUMBER = '/platform/sys/user/number'
export const API_SYS_USER_GROUP = '/platform/sys/user/group'
export const API_SYS_USER_CREATE = '/platform/sys/user/create'
export const API_SYS_USER_GET = '/platform/sys/user/get/'
export const API_SYS_USER_UPDATE = '/platform/sys/user/update'
export const API_SYS_USER_RESETPWD = '/platform/sys/user/reset_pwd/'
export const API_SYS_USER_DISABLED = '/platform/sys/user/disabled'
export const API_SYS_USER_DELETE = '/platform/sys/user/delete/'
export const API_SYS_USER_DELETE_MORE = '/platform/sys/user/delete_more'
export const API_SYS_USER_EXPORT = '/platform/sys/user/export'
export const API_SYS_USER_IMPORT_DATA = '/platform/sys/user/importData'
export const API_SYS_USER_IMPORT_TEMPLATE = '/platform/sys/user/importTemplate'

import request from '/@/utils/request'

export function getUnitList(data: object) {
    return request({
        url: API_SYS_USER_UNITLIST,
        method: 'GET',
        params: data
    })
}

export function getList(data: object) {
    return request({
        url: API_SYS_USER_LIST,
        method: 'POST',
        data: data
    })
}

export function getPostList() {
    return request({
        url: API_SYS_USER_POST,
        method: 'GET'
    })
}

export function getInfo(id: string) {
    return request({
        url: API_SYS_USER_GET + id,
        method: 'GET'
    })
}

export function getSerialNo() {
    return request({
        url: API_SYS_USER_NUMBER,
        method: 'GET'
    })
}

export function getRoleGroups(id: string) {
    return request({
        url: API_SYS_USER_GROUP,
        method: 'GET',
        params: {unitId: id}
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_USER_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_USER_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doResetPwd(id: string) {
    return request({
        url: API_SYS_USER_RESETPWD + id,
        method: 'GET'
    })
}

export function doDelete(id: string, loginname: string) {
    return request({
        url: API_SYS_USER_DELETE + id,
        method: 'DELETE',
        data: { loginname: loginname}
    })
}

export function doDeleteMore(ids: string, names: string) {
    return request({
        url: API_SYS_USER_DELETE_MORE,
        method: 'POST',
        data: { ids: ids, names: names}
    })
}

export function doDisable(data: object = {}) {
    return request({
        url: API_SYS_USER_DISABLED,
        method: 'POST',
        data: data
    })
}