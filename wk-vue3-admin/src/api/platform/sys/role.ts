export const API_SYS_ROLE_GET = '/platform/sys/role/get/'
export const API_SYS_ROLE_DELETE = '/platform/sys/role/delete'
export const API_SYS_ROLE_CREATE = '/platform/sys/role/create'
export const API_SYS_ROLE_UPDATE = '/platform/sys/role/update'
export const API_SYS_ROLE_USERLIST = '/platform/sys/role/user'
export const API_SYS_ROLE_GET_DO_MENU = '/platform/sys/role/get_menus'
export const API_SYS_ROLE_DO_MENU = '/platform/sys/role/do_menu'
export const API_SYS_ROLE_UNIT = '/platform/sys/role/unit'
export const API_SYS_ROLE_GROUP = '/platform/sys/role/group'
export const API_SYS_ROLE_APP = '/platform/sys/role/app'
export const API_SYS_ROLE_POST = '/platform/sys/role/post'
export const API_SYS_ROLE_SELECT_USER = '/platform/sys/role/select_user'
export const API_SYS_ROLE_LINK_USER = '/platform/sys/role/link_user'
export const API_SYS_ROLE_UNLINK_USER = '/platform/sys/role/unlink_user'

import request from '/@/utils/request'


export function getUserList(data: object) {
    return request({
        url: API_SYS_ROLE_USERLIST,
        method: 'POST',
        data: data
    })
}

export function getQueryUserList(data: object) {
    return request({
        url: API_SYS_ROLE_SELECT_USER,
        method: 'POST',
        data: data
    })
}

export function getGroupList(unitId: string) {
    return request({
        url: API_SYS_ROLE_GROUP,
        method: 'GET',
        params: { unitId: unitId}
    })
}

export function getUnitList() {
    return request({
        url: API_SYS_ROLE_UNIT,
        method: 'GET'
    })
}

export function getAppList() {
    return request({
        url: API_SYS_ROLE_APP,
        method: 'GET'
    })
}

export function getPostList() {
    return request({
        url: API_SYS_ROLE_POST,
        method: 'GET'
    })
}

export function getMenuList(roleId: string,appId: string) {
    return request({
        url: API_SYS_ROLE_GET_DO_MENU,
        method: 'GET',
        params: {roleId: roleId, appId: appId}
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_ROLE_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_ROLE_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doMenu(data: object = {}) {
    return request({
        url: API_SYS_ROLE_DO_MENU,
        method: 'POST',
        data: data
    })
}

export function doDelete(type: string, id: string, name: string) {
    return request({
        url: API_SYS_ROLE_DELETE,
        method: 'POST',
        data: { type: type, id: id, name: name }
    })
}

export function doLinkUser(roleId: string, roleCode: string, ids: string, names: string) {
    return request({
        url: API_SYS_ROLE_LINK_USER,
        method: 'POST',
        data: {
            roleId: roleId,
            roleCode: roleCode,
            ids: ids,
            names: names
        }
    })
}

export function doUnLinkUser(roleId: string, roleCode: string, id: string, name: string) {
    return request({
        url: API_SYS_ROLE_UNLINK_USER,
        method: 'POST',
        data: {
            roleId: roleId,
            roleCode: roleCode,
            id: id,
            name: name
        }
    })
}