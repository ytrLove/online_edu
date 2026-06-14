import axios from 'axios'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // API基础URL
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从cookie获取token
    const token = Cookies.get('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果返回的状态码不是200，说明接口请求有误
    if (res.code !== 200) {
      ElMessage({
        message: res.msg || '请求错误',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        // 跳转到登录页
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.msg || '请求错误'))
    } else {
      return res
    }
  },
  error => {
    console.log('请求错误: ' + error)
    ElMessage({
      message: error.message || '请求错误',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service 