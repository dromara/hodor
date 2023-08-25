import axios from 'axios'
import { message } from 'ant-design-vue';
import JSONBIG from 'json-bigint'

// 设置响应数据的转换函数,使用JSONBIG解析响应数据,解决精度丢失问题
axios.defaults.transformResponse = [
  function (data) {
    const json = JSONBIG({
      storeAsString: true
    })
    const res = json.parse(data)
    return res
  }
]

// 创建axios实例
const httpInstance = axios.create({
  baseURL: window.location.origin + '/hodor/admin',  // 根地址
  timeout: 5000  // 超时时间
})

// axios请求拦截器
httpInstance.interceptors.request.use(config => {
  // 请求头添加API-KEY
  const apiKey = 'b50fd4d4d71935b7c2a001b87f068c4f'
  config.headers['API-KEY'] = apiKey
  return config
}, error => Promise.reject(error))

// 处理响应失败函数
const catchFalse = function (response) {
  const res = response.data
  if (res.successful) {
    return res
  } else {
    message.error(res.msg)
    return Promise.reject(res.msg)
  }
}
// 处理错误回调函数
const catchError = function (error) {
  if (error.response) {
    switch (error.response.status) {
      case 400:
        message.error(error.response.data.msg || '请求参数异常')
        break
      case 401:
        message.warning(error.response.data.msg || '密码错误或账号不存在！')
        break
      case 403:
        message.warning(error.response.data.msg || '无访问权限，请联系企业管理员')
        break
      default:
        message.error(error.response.data.msg || '服务端异常，请联系技术支持')
    }
  }
  return Promise.reject(error)
}
// axios响应式拦截器
httpInstance.interceptors.response.use(catchFalse, catchError)



export default httpInstance