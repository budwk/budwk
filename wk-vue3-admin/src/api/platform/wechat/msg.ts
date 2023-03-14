export const API_WX_MSG_USER_LIST = '/wechat/admin/msg/user/list'
export const API_WX_MSG_USER_REPLY = '/wechat/admin/msg/user/reply/'


import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_MSG_USER_LIST,
        method: 'POST',
        data: data
    })
}

export function doReply(wxid: string, data: object) {
    return request({
        url: API_WX_MSG_USER_REPLY + wxid,
        method: 'POST',
        data: data
    })
}