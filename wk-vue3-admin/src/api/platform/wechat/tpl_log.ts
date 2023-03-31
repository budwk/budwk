export const API_WX_TPL_LOG_LIST = '/wechat/admin/tpl/log/list'

import request from '/@/utils/request'

export function getList(data: object) {
    return request({
        url: API_WX_TPL_LOG_LIST,
        method: 'POST',
        data: data
    })
}
