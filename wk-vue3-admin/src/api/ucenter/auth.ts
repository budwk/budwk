export const API_AUTH_LOGIN = '/ucenter/auth/login'
export const API_AUTH_CAPTCHA = '/ucenter/auth/captcha'
export const API_AUTH_RSA = '/ucenter/auth/rsa'
export const API_AUTH_SMSCODE = '/ucenter/auth/smscode'
export const API_AUTH_LOGOUT = '/ucenter/auth/logout'
export const API_AUTH_INFO = '/ucenter/auth/info'
export const API_AUTH_CHECK_LOGINNAME = '/ucenter/auth/check/loginname'
export const API_AUTH_PWD_SENDCODE = '/ucenter/auth/pwd/sendcode'
export const API_AUTH_PWD_SAVE = '/ucenter/auth/pwd/save'
export const API_AUTH_SET_THEME = '/ucenter/auth/theme'
export const API_AUTH_CHECKPWD = '/ucenter/auth/checkpwd'

import request from '/@/utils/request'

export function getCaptcha() {
    return request({
        url: API_AUTH_CAPTCHA,
        method: 'GET'
    })
}

export function getRsa() {
    return request({
        url: API_AUTH_RSA,
        method: 'GET'
    })
}

export function getSmsCode(mobile: string) {
    return request({
        url: API_AUTH_SMSCODE,
        method: 'POST',
        data: { mobile: mobile}
    })
}

export function checkLoginname(loginname: string) {
    return request({
        url: API_AUTH_CHECK_LOGINNAME,
        method: 'POST',
        data: { loginname: loginname}
    })
}

export function sendResetPwdCode(data: object = {}) {
    return request({
        url: API_AUTH_PWD_SENDCODE,
        method: 'POST',
        data: data
    })
}

export function saveNewPwd(data: object = {}) {
    return request({
        url: API_AUTH_PWD_SAVE,
        method: 'POST',
        data: data
    })
}

export function doLogin(data: object = {}) {
    return request({
        url: API_AUTH_LOGIN,
        method: 'POST',
        data: data
    })
}

export function getUserInfo() {
    return request({
        url: API_AUTH_INFO,
        method: 'GET'
    })
}

export function logout() {
    return request({
        url: API_AUTH_LOGOUT,
        method: 'GET'
    })
}
