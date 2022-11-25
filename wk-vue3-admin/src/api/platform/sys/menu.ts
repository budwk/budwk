export const API_SYS_MENU_LIST = '/platform/sys/menu/list'
export const API_SYS_MENU_TREE = '/platform/sys/menu/tree'
export const API_SYS_MENU_DISABLED = '/platform/sys/menu/disabled'
export const API_SYS_MENU_CREATE = '/platform/sys/menu/create'
export const API_SYS_MENU_DELETE = '/platform/sys/menu/delete/'
export const API_SYS_MENU_GET_MENU = '/platform/sys/menu/get_menu/'
export const API_SYS_MENU_UPDATE_MENU = '/platform/sys/menu/update_menu'
export const API_SYS_MENU_GET_DATA = '/platform/sys/menu/get_data/'
export const API_SYS_MENU_UPDATE_DATA = '/platform/sys/menu/update_data'
export const API_SYS_MENU_SORT_TREE = '/platform/sys/menu/get_sort_tree'
export const API_SYS_MENU_SORT = '/platform/sys/menu/sort'
export const API_SYS_MENU_APP_DATA = '/platform/sys/menu/data'


import request from '/@/utils/request'

export function getAppList() {
    return request({
        url: API_SYS_MENU_APP_DATA,
        method: 'GET'
    })
}


export function getList(params: object = {}) {
    return request({
        url: API_SYS_MENU_LIST,
        method: 'GET',
        params: params
    })
}


export function getInfo(id: string) {
    return request({
        url: API_SYS_MENU_GET_MENU + id,
        method: 'GET'
    })
}

export function doCreate(menu: string,buttons: string, appId: string) {
    return request({
        url: API_SYS_MENU_CREATE,
        method: 'POST',
        data: {menu: menu, buttons: buttons, appId: appId}
    })
}

export function doUpdate(menu: string, buttons: string ) {
    return request({
        url: API_SYS_MENU_UPDATE_MENU,
        method: 'POST',
        data: { menu: menu, buttons: buttons}
    })
}

export function doUpdateData(data: object = {}) {
    return request({
        url: API_SYS_MENU_UPDATE_DATA,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_MENU_DELETE + id,
        method: 'DELETE'
    })
}

export function getSortTree() {
    return request({
        url: API_SYS_MENU_SORT_TREE,
        method: 'GET'
    })
}

export function doSort(ids: string, appId: string) {
    return request({
        url: API_SYS_MENU_SORT,
        method: 'POST',
        data: { ids: ids, appId: appId}
    })
}

export function doDisable(data: object = {}) {
    return request({
        url: API_SYS_MENU_DISABLED,
        method: 'POST',
        data: data
    })
}