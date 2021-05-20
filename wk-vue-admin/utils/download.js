import axios from 'axios'
import { Notification } from 'element-ui'
const Cookie = process.browser ? require('js-cookie') : undefined
const service = axios.create({
  baseURL: process.server ? process.env.API : '',
  timeout: 15000, // 请求超时时间
  responseType: 'arraybuffer'
})

service.defaults.withCredentials = true // 让ajax携带cookie

service.interceptors.response.use(
  (response) => {
    const token = response.headers.authorization
    if (token) {
      response.data.token = token
      if (Cookie) {
        Cookie.set('auth', token)
      }
    }

    if (response.headers['content-type'].indexOf('json') !== -1) {
      const text = Buffer.from(response.data).toString('utf8')
      const json = JSON.parse(text)

      if (json.code !== 1) {
        if (!process.server) {
          Notification.error({
            title: '错误4',
            message: json.message
          })
        }

        return Promise.reject(json.message)
      }
    } else {
      const fileDownload = require('js-file-download')
      fileDownload(response.data, '1.xlsx')
    }
  },
  (error) => {
    console.log(error)
    return Promise.reject(error)
  }
)

export default service
