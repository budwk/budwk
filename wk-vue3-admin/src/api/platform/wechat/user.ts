export const API_WX_USER_LIST = '/wechat/admin/user/list'
export const API_WX_USER_DOWN = '/wechat/admin/user/down/'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_USER_LIST,
        method: 'POST',
        data: data
    })
}

export function doDownload(wxid: string) {
    return request({
        url: API_WX_USER_DOWN + wxid,
        method: 'GET'
    })
}
