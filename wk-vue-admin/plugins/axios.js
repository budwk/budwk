import { Notification } from 'element-ui'
import { SUCCESS_CODE } from '@/constant/code'

export default function({ $axios, redirect, store, app }) {
  $axios.defaults.baseURL = process.env.API
  $axios.onRequest((config) => {
    $axios.setHeader('X-Token', app.$cookies.get('X-Token'))
    // console.log('发起请求::' + config.url)
  })

  $axios.onResponse((res) => {
    if (res.data.code) {
      if (res.data.code !== SUCCESS_CODE) {
        if (process.browser) {
          // 表单验证失败,打印表单错误信息
          if (res.data.code === 500200) {
            var note = ''
            for (var key in res.data.data) {
              var tempName = res.data.data[key].name
              if (!tempName) {
                tempName = key
              }
              note += tempName + ' : ' + res.data.data[key].msg + ' \r\n'
            }
            Notification.error({
              title: res.data.msg,
              message: note
            })
          } else {
            Notification.error({
              title: '系统错误',
              message: res.data.msg
            })
          }
        }
      }
      // 登录过期删除token信息
      if (res.data.code === 600098) {
        app.$cookies.remove('X-Token')
        app.store.commit('setAuth', '')
      }
      return Promise.resolve(res)
    } else {
      return Promise.resolve(res)
    }
  })

  $axios.onError((error) => {
    // const code = parseInt(error.response && error.response.status)
    // if (code === 400) {

    // }
    return Promise.reject(error)
  })
}
