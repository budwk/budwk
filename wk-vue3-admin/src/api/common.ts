import request from '../utils/request'
import { isPlatformApp, checkFileMimetype } from '/@/utils/common'
import { getUrl } from '../utils/request'
import { useUserInfo } from '/@/stores/userInfo'
import { ElNotification, UploadRawFile } from 'element-plus'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { i18n } from '../lang'

/*
 * 公共请求函数和Url定义
 */
// Platform模块
export const platformInfoUrl = '/ucenter/auth/conf'


// Admin模块
export const adminUploadUrl = '/admin/ajax/upload'
export const adminBuildSuffixSvgUrl = '/admin/ajax/buildSuffixSvg'
export const adminAreaUrl = '/admin/ajax/area'
export const getTablePkUrl = '/admin/ajax/getTablePk'
export const clearCacheUrl = '/admin/ajax/clearCache'

// 公共
export const captchaUrl = '/api/common/captcha'

// api模块(前台)
export const apiUploadUrl = '/api/ajax/upload'
export const apiBuildSuffixSvgUrl = '/api/ajax/buildSuffixSvg'
export const apiAreaUrl = '/api/ajax/area'
export const apiSendSms = '/api/Sms/send'
export const apiSendEms = '/api/Ems/send'

/**
 * 获取地区数据
 */
export function getPlatformInfo(appId: string) {
    const params: { appId?: string } = {}
    params.appId = appId
    return request({
        url: platformInfoUrl,
        method: 'GET',
        params: params,
    })
}

/**
 * 上传文件
 */
export function fileUpload(fd: FormData, params: anyObj = {}, forceLocal = false): ApiPromise {
    let errorMsg = ''
    const file = fd.get('file') as UploadRawFile
    const platformInfo = usePlatformInfo()

    if (!file.name || typeof file.size == 'undefined') {
        errorMsg = i18n.global.t('utils.The data of the uploaded file is incomplete!')
    } else if (!checkFileMimetype(file.name, file.type)) {
        errorMsg = i18n.global.t('utils.The type of uploaded file is not allowed!')
    } else if (file.size > platformInfo.upload.maxsize) {
        errorMsg = i18n.global.t('utils.The size of the uploaded file exceeds the allowed range!')
    }
    if (errorMsg) {
        return new Promise((resolve, reject) => {
            ElNotification({
                type: 'error',
                message: errorMsg,
            })
            reject(errorMsg)
        })
    }

    // if (!forceLocal && uploadExpandState() == 'enable') {
    //     return uploadExpand(fd, params)
    // }

    return request({
        url: isPlatformApp() ? adminUploadUrl : apiUploadUrl,
        method: 'POST',
        data: fd,
        params: params,
    }) as ApiPromise
}

/**
 * 获取地区数据
 */
export function getArea(values: number[]) {
    const params: { province?: number; city?: number } = {}
    if (values[0]) {
        params.province = values[0]
    }
    if (values[1]) {
        params.city = values[1]
    }
    return request({
        url: isPlatformApp() ? adminAreaUrl : apiAreaUrl,
        method: 'GET',
        params: params,
    })
}

/**
 * 发送短信
 */
export function sendSms(mobile: string, templateCode: string) {
    return request(
        {
            url: apiSendSms,
            method: 'POST',
            data: {
                mobile: mobile,
                template_code: templateCode,
            },
        },
        {
            showSuccessMessage: true,
        }
    ) as ApiPromise
}

/**
 * 发送邮件
 */
export function sendEms(email: string, event: string) {
    return request(
        {
            url: apiSendEms,
            method: 'POST',
            data: {
                email: email,
                event: event,
            },
        },
        {
            showSuccessMessage: true,
        }
    ) as ApiPromise
}
