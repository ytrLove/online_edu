import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken, clearUserInfo } from '@/utils/auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 是否正在显示登录过期提示
let isShowingLoginExpired = false

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的是文件流，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 根据状态码处理响应
    if (res.code === 200) {
      return res
    } else {
      // 显示错误消息，除非是401错误（未授权）
      if(res.code !== 401) {
      ElMessage({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })
      }
      
      // 处理特定错误码
      if (res.code === 401) {
        // 未授权，需要重新登录
        handleUnauthorized()
      }
      
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    const { status, data } = error.response || {}
    
    // 根据状态码处理错误
    switch (status) {
      case 401:
        // 未授权，需要重新登录
        handleUnauthorized()
        break
      case 403:
        ElMessage({
          message: '没有权限访问该资源',
          type: 'error',
          duration: 5 * 1000
        })
        break
      case 404:
        ElMessage({
          message: '请求的资源不存在',
          type: 'error',
          duration: 5 * 1000
        })
        break
      case 500:
        ElMessage({
          message: '服务器错误，请稍后再试',
          type: 'error',
          duration: 5 * 1000
        })
        break
      default:
        // 只有在有响应的情况下才显示消息
        if (error.response) {
        ElMessage({
          message: data?.msg || '请求失败，请稍后再试',
          type: 'error',
          duration: 5 * 1000
        })
        }
    }
    
    return Promise.reject(error)
  }
)

// 处理未授权情况
function handleUnauthorized() {
  // 避免多次显示登录过期提示
  if (isShowingLoginExpired) {
    return
  }
  
  // 如果在登录页，不需要显示提示
  if (router.currentRoute.value.path === '/login') {
    clearUserInfo()
    return
  }
  
  isShowingLoginExpired = true
  
  ElMessageBox.confirm(
    '登录状态已过期，是否重新登录？',
    '登录提示',
    {
      confirmButtonText: '重新登录',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    clearUserInfo()
    router.push('/login')
  }).catch(() => {
    // 用户取消了操作
  }).finally(() => {
    isShowingLoginExpired = false
  })
}

export default service