
export const API_HOME_USER_CHANGE_PWD = '/platform/home/user/pwd'
export const API_HOME_USER_CHANGE_INFO = '/platform/home/user/info'
export const API_HOME_USER_GET_INFO = '/platform/home/user/get'
export const API_HOME_USER_SET_AVATAR = '/platform/home/user/avatar'
export const API_HOME_USER_GET_LOG = '/platform/home/user/log'

import request from '/@/utils/request'

export function doChangePwd(oldPassword: string,newPassword: string) {
    return request({
        url: API_HOME_USER_CHANGE_PWD,
        method: 'POST',
        data: {oldPassword: oldPassword, newPassword: newPassword}
    })
}

export function doChangeInfo(data: object) {
    return request({
        url: API_HOME_USER_CHANGE_INFO,
        method: 'POST',
        data: data
    })
}

export function doChangeAvatar(avatar: string) {
    return request({
        url: API_HOME_USER_SET_AVATAR,
        method: 'POST',
        data: {avatar: avatar}
    })
}

export function getUserInfo() {
    return request({
        url: API_HOME_USER_GET_INFO,
        method: 'GET',
    })
}

export function getUserLog(data: object) {
    return request({
        url: API_HOME_USER_GET_LOG,
        method: 'POST',
        data: data
    })
}