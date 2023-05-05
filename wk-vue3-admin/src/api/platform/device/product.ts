export const API_DEVICE_PRODUCT_INIT = '/device/admin/product/init'
export const API_DEVICE_PRODUCT_LIST = '/device/admin/product/list'
export const API_DEVICE_PRODUCT_GET = '/device/admin/product/get/'
export const API_DEVICE_PRODUCT_CREATE = '/device/admin/product/create'
export const API_DEVICE_PRODUCT_DELETE = '/device/admin/product/delete/'
export const API_DEVICE_PRODUCT_UPDATE = '/device/admin/product/update'

import request from '/@/utils/request'

export function getInit() {
    return request({
        url: API_DEVICE_PRODUCT_INIT,
        method: 'GET'
    })
}

export function getList(data: object = {}) {
    return request({
        url: API_DEVICE_PRODUCT_LIST,
        method: 'POST',
        data: data
    })
}


export function getInfo(id: string) {
    return request({
        url: API_DEVICE_PRODUCT_GET + id,
        method: 'GET'
    })
}

export function doCreate(data: object = {}) {
    return request({
        url: API_DEVICE_PRODUCT_CREATE,
        method: 'POST',
        data: data
    })
}

export function doUpdate(data: object = {}) {
    return request({
        url: API_DEVICE_PRODUCT_UPDATE,
        method: 'POST',
        data: data
    })
}

export function doDelete(id: string) {
    return request({
        url: API_DEVICE_PRODUCT_DELETE + id,
        method: 'DELETE'
    })
}

export const auth_MQTT = [{name:'username',value:'',note:'用户名' },{name:'password',value:'',note:'密码' }]
export const auth_AEP_HTTP = [{name:'masterKey',value:'',note:'AEP产品masterKey' },{name:'productId',value:'',note:'AEP产品productId' },{name:'hasProfile',value:'',note:'AEP产品是否有Profile文件(true|false)' }]
export const auth_AEP_MQ = [{name:'productId',value:'',note:'AEP产品productId' }]