import axios from 'axios'
import { message } from 'ant-design-vue';

// 创建axios实例
const httpInstance = axios.create({
  baseURL: '',  // 根地址
  timeout: 5000  // 超时时间
})

// axios请求拦截器
httpInstance.interceptors.request.use(config => {
  return config
}, e => Promise.reject(e))

// 处理错误回调函数
const catchError = function (error) {
  if (error.response) {
    switch (error.response.status) {
      case 400:
        message.error(error.response.data.message || '请求参数异常')
        break
      case 401:
        message.warning(error.response.data.message || '密码错误或账号不存在！')
        break
      case 403:
        message.warning(error.response.data.message || '无访问权限，请联系企业管理员')
        break
      default:
        message.error(error.response.data.message || '服务端异常，请联系技术支持')
    }
  }
  return Promise.reject(error)
}
// axios响应式拦截器
httpInstance.interceptors.response.use(res => res.data, catchError)



export default httpInstance