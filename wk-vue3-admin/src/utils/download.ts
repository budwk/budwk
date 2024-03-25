import axios from 'axios'
import { ElLoading, ElMessage, ElNotification } from 'element-plus'
import { saveAs } from 'file-saver'
import { useUserInfo } from '/@/stores/userInfo'
import { blobValidate } from '/@/utils/common'

const baseURL = '' + import.meta.env.VITE_AXIOS_BASE_URL
let downloadLoadingInstance: any
  
export default {
    name(name: string, isDelete = true) {
        const url = baseURL + "/common/download?fileName=" + encodeURI(name) + "&delete=" + isDelete
        const userInfo = useUserInfo()
        axios({
            method: 'get',
            url: url,
            responseType: 'blob',
            headers: { 'wk-user-token': userInfo.getToken() }
        }).then(async (res) => {
            const isLogin = await blobValidate(res.data);
            if (isLogin) {
                const blob = new Blob([res.data])
                this.saveAs(blob, decodeURI(res.headers['download-filename']))
            } else {
                this.printErrMsg(res.data);
            }
        })
    },
    resource(resource: any) {
        const url = baseURL + "/common/download/resource?resource=" + encodeURI(resource);
        const userInfo = useUserInfo()
        axios({
            method: 'get',
            url: url,
            responseType: 'blob',
            headers: { 'wk-user-token': userInfo.getToken() }
        }).then(async (res) => {
            const isLogin = await blobValidate(res.data);
            if (isLogin) {
                const blob = new Blob([res.data])
                this.saveAs(blob, decodeURI(res.headers['download-filename']))
            } else {
                this.printErrMsg(res.data);
            }
        })
    },
    zip(url: string, name: string) {
        const newurl = baseURL + url
        const userInfo = useUserInfo()

        axios({
            method: 'get',
            url: newurl,
            responseType: 'blob',
            headers: { 'wk-user-token': userInfo.getToken() }
        }).then(async (res) => {
            const isLogin = await blobValidate(res.data)
            if (isLogin) {
                const blob = new Blob([res.data], { type: 'application/zip' })
                this.saveAs(blob, name)
            } else {
                this.printErrMsg(res.data);
            }
        })
    },
    saveAs(text: any, name: any, opts: any) {
        saveAs(text, name, opts);
    },
    async printErrMsg(data: any) {
        const resText = await data.text();
        const rspObj = JSON.parse(resText);
        const errorCode = {
            '401': '认证失败，无法访问系统资源',
            '403': '当前操作没有权限',
            '404': '访问资源不存在',
            'default': '系统未知错误，请反馈给管理员'
        }
        const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
        ElMessage.error(errMsg);
    },
    // 通用下载方法
    download(url: string, params: any, filename: string, config = undefined) {
        const userInfo = useUserInfo()

        downloadLoadingInstance = ElLoading.service({ text: "正在下载数据，请稍候", background: "rgba(0, 0, 0, 0.7)", })
        const service = axios.create({
            // axios中请求配置有baseURL选项，表示请求URL公共部分
            baseURL: baseURL,
            // 超时
            timeout: 100 * 1000 
        })
        return service.post(url, params, {
            transformRequest: [(params) => { return this.tansParams(params) }],
            headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'wk-user-token': userInfo.getToken() },
            responseType: 'blob',
            ...config
        }).then(async (res: any) => {
            //console.log(res)
            const isLogin = await blobValidate(res)
            //console.log(isLogin)

            if (isLogin) {
                const blob = new Blob([res.data])
                saveAs(blob, filename)
            } else {
                const resText = await res.data.text();
                const rspObj = JSON.parse(resText);
                this.printErrMsg(rspObj)
            }
            downloadLoadingInstance.close()
        }).catch((r) => {
            console.error(r)
            ElNotification({
                type: 'error',
                message: '下载文件出现错误，请联系管理员！',
            })
            downloadLoadingInstance.close()
        })
    },
    /**
    * 参数处理
    * @param {*} params  参数
    */
    tansParams(params: any) {
        let result = ''
        for (const propName of Object.keys(params)) {
            const value = params[propName];
            const part = encodeURIComponent(propName) + "=";
            if (value !== null && value !== "" && typeof (value) !== "undefined") {
                if (typeof value === 'object') {
                    for (const key of Object.keys(value)) {
                        if (value[key] !== null && value[key] !== "" && typeof (value[key]) !== 'undefined') {
                            const params = propName + '[' + key + ']';
                            const subPart = encodeURIComponent(params) + "=";
                            result += subPart + encodeURIComponent(value[key]) + "&";
                        }
                    }
                } else {
                    result += part + encodeURIComponent(value) + "&";
                }
            }
        }
        return result
    }
}
