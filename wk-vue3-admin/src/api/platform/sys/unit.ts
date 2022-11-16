export const API_SYS_UNIT_CHILD = '/platform/sys/unit/child'
export const API_SYS_UNIT_LIST = '/platform/sys/unit/list'
export const API_SYS_UNIT_CREATE = '/platform/sys/unit/create'
export const API_SYS_UNIT_DELETE = '/platform/sys/unit/delete/'
export const API_SYS_UNIT_GET = '/platform/sys/unit/get/'
export const API_SYS_UNIT_UPDATE = '/platform/sys/unit/update'
export const API_SYS_UNIT_SORT_TREE = '/platform/sys/unit/get_sort_tree'
export const API_SYS_UNIT_SORT = '/platform/sys/unit/sort'
export const API_SYS_UNIT_SEARCH_USER = '/platform/sys/unit/search_user'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_UNIT_LIST,
        method: 'GET',
        params: data
    })
}

export function getChild(pid: string) {
    return request({
        url: API_SYS_UNIT_CHILD,
        method: 'GET',
        params: { pid: pid}
    })
}

export function getInfo(id: string) {
    return request({
        url: API_SYS_UNIT_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_UNIT_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_UNIT_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_UNIT_DELETE + id,
        method: 'DELETE'
    })
}

export function getSortTree() {
    return request({
        url: API_SYS_UNIT_SORT_TREE,
        method: 'GET'
    })
}

export function doSort(ids: string) {
    return request({
        url: API_SYS_UNIT_SORT,
        method: 'POST',
        data: { ids: ids}
    })
}

export function doSearchUser(query: string, unitId: string) {
    return request({
        url: API_SYS_UNIT_SEARCH_USER,
        method: 'POST',
        data: { query: query, unitId: unitId}
    })
}