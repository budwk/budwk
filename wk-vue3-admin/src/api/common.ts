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
export const platformUploadUrl = '/platform/pub/file/upload/'
export const platformUploadImageUrl = '/platform/pub/file/upload/image'
export const platformUploadFileUrl = '/platform/pub/file/upload/file'
export const platformUploadVideoUrl = '/platform/pub/file/upload/video'


// Public模块


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
export function fileUpload(fd: FormData, params: anyObj = {}, type = 'file', forceLocal = false): ApiPromise {
    let errorMsg = ''
    console.log(fd.get('Filedata'))
    const file = fd.get('Filedata') as UploadRawFile
    const platformInfo = usePlatformInfo()

    if (!file.name || typeof file.size == 'undefined') {
        errorMsg = i18n.global.t('utils.The data of the uploaded file is incomplete!')
    } else if (file.size > parseInt(platformInfo.AppUploadSize) * 1024 ) {
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

    if (!forceLocal && uploadExpandState() == 'enable') {
        return uploadExpand(fd, params)
    }
    if(isPlatformApp()) { //后台上传
        const userInfo = useUserInfo()

        return request({
            url: platformUploadUrl + type,
            method: 'POST',
            data: fd,
            headers: { "wk-user-token": userInfo.getToken() },
            params: params,
        }) as ApiPromise
    } else {
        return request({
            url: platformUploadUrl + type,
            method: 'POST',
            data: fd,
            params: params,
        }) as ApiPromise
    }
}

/**
 * 上传文件
 */
export function fileUploadExt(fd: FormData, params: anyObj = {}, url = '', size = 1024): ApiPromise {
    let errorMsg = ''
    const file = fd.get('Filedata') as UploadRawFile
    if (!file.name || typeof file.size == 'undefined') {
        errorMsg = i18n.global.t('utils.The data of the uploaded file is incomplete!')
    } else if (file.size > size * 1024 ) {
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
    const userInfo = useUserInfo()

    return request({
        url: url,
        method: 'POST',
        data: fd,
        headers: { "wk-user-token": userInfo.getToken() },
        params: params,
    }) as ApiPromise
}


const uploadExpandState: () => 'disable' | 'enable' = () => 'disable'

const uploadExpand = (fd: FormData, params: anyObj = {}): ApiPromise => {
    // 上传扩展，定义此函数，并将上方的 state 设定为 enable，系统可自动使用此函数进行上传
    return new Promise((resolve, reject) => {
        console.log(fd, params)
        reject('未定义')
    })
}