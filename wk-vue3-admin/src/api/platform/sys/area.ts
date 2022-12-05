export const API_SYS_AREA_LIST = '/platform/sys/area/list'
export const API_SYS_AREA_CHILD = '/platform/sys/area/child'
export const API_SYS_AREA_TREE = '/platform/sys/area/tree'
export const API_SYS_AREA_DISABLED = '/platform/sys/area/disabled'
export const API_SYS_AREA_CREATE = '/platform/sys/area/create'
export const API_SYS_AREA_DELETE = '/platform/sys/area/delete/'
export const API_SYS_AREA_GET = '/platform/sys/area/get/'
export const API_SYS_AREA_UPDATE = '/platform/sys/area/update'
export const API_SYS_AREA_SORT = '/platform/sys/area/sort'

import request from '/@/utils/request'

export function getList() {
    return request({
        url: API_SYS_AREA_LIST,
        method: 'GET'
    })
}

export function getChild(pid: string) {
    return request({
        url: API_SYS_AREA_CHILD,
        method: 'GET',
        params: { pid: pid}
    })
}

export function getInfo(id: string) {
    return request({
        url: API_SYS_AREA_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_AREA_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_AREA_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_AREA_DELETE + id,
        method: 'DELETE'
    })
}

export function getSortTree() {
    return request({
        url: API_SYS_AREA_SORT_TREE,
        method: 'GET'
    })
}

export function doSort(ids: string) {
    return request({
        url: API_SYS_AREA_SORT,
        method: 'POST',
        data: { ids: ids}
    })
}

export function doDisable(data: object = {}) {
    return request({
        url: API_SYS_AREA_DISABLED,
        method: 'POST',
        data: data
    })
}