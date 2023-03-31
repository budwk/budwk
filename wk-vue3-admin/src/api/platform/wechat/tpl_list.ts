export const API_WX_TPL_LIST_GET = '/wechat/admin/tpl/list/get_all_template/'
export const API_WX_TPL_LIST_LIST = '/wechat/admin/tpl/list/list'


import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_TPL_LIST_LIST,
        method: 'POST',
        data: data
    })
}

export function doDownload(wxid: string) {
    return request({
        url: API_WX_TPL_LIST_GET + wxid,
        method: 'GET'
    })
}