export const API_SYS_DICT_LIST = '/platform/sys/dict/list'
export const API_SYS_DICT_CHILD = '/platform/sys/dict/child'
export const API_SYS_DICT_TREE = '/platform/sys/dict/tree'
export const API_SYS_DICT_DISABLED = '/platform/sys/dict/disabled'
export const API_SYS_DICT_CREATE = '/platform/sys/dict/create'
export const API_SYS_DICT_DELETE = '/platform/sys/dict/delete/'
export const API_SYS_DICT_GET = '/platform/sys/dict/get/'
export const API_SYS_DICT_UPDATE = '/platform/sys/dict/update'
export const API_SYS_DICT_SORT_TREE = '/platform/sys/dict/get_sort_tree'
export const API_SYS_DICT_SORT = '/platform/sys/dict/sort'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_SYS_DICT_LIST,
        method: 'GET',
        params: data
    })
}

export function getChild(pid: string) {
    return request({
        url: API_SYS_DICT_CHILD,
        method: 'GET',
        params: { pid: pid}
    })
}

export function getInfo(id: string) {
    return request({
        url: API_SYS_DICT_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_SYS_DICT_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_SYS_DICT_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_SYS_DICT_DELETE + id,
        method: 'DELETE'
    })
}

export function getSortTree() {
    return request({
        url: API_SYS_DICT_SORT_TREE,
        method: 'GET'
    })
}

export function doSort(ids: string) {
    return request({
        url: API_SYS_DICT_SORT,
        method: 'POST',
        data: { ids: ids}
    })
}

export function doDisable(data: object = {}) {
    return request({
        url: API_SYS_DICT_DISABLED,
        method: 'POST',
        data: data
    })
}