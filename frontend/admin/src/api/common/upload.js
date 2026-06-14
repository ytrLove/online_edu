import request from '@/utils/request'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 云服务器上传基础URL
const UPLOAD_BASE_URL = 'http://110.41.15.40:4000/api/upload'

/**
 * 创建上传请求实例
 * @returns {AxiosInstance} Axios实例
 */
const createUploadInstance = () => {
  const instance = axios.create({
    baseURL: UPLOAD_BASE_URL,
    timeout: 60000, // 上传文件可能较大，延长超时时间
    headers: {
      'Authorization': `Bearer ${getToken()}`
    }
  })

  // 添加响应拦截器处理常见错误
  instance.interceptors.response.use(
    response => response,
    error => {
      if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
        ElMessage.error('上传超时，请检查网络连接或尝试上传小一些的文件')
      } else if (!error.response) {
        ElMessage.error('网络错误，无法连接到服务器')
      } else {
        const status = error.response.status
        if (status === 401) {
          ElMessage.error('认证失败，请重新登录')
        } else if (status === 413) {
          ElMessage.error('文件太大，超出服务器接收限制')
        } else {
          ElMessage.error(`上传失败(${status}): ${error.response.data?.message || '未知错误'}`)
        }
      }
      return Promise.reject(error)
    }
  )

  return instance
}

/**
 * 重试上传函数
 * @param {Function} uploadFn 上传函数
 * @param {Number} maxRetries 最大重试次数
 * @returns {Promise} 返回Promise对象
 */
const withRetry = (uploadFn, maxRetries = 2) => {
  return async (...args) => {
    let lastError
    for (let i = 0; i <= maxRetries; i++) {
      try {
        return await uploadFn(...args)
      } catch (error) {
        lastError = error
        // 如果是网络错误或超时错误，则重试
        if (error.code === 'ECONNABORTED' || 
            error.message.includes('timeout') || 
            !error.response) {
          if (i < maxRetries) {
            console.log(`上传重试(${i + 1}/${maxRetries})...`)
            // 等待一小段时间再重试
            await new Promise(resolve => setTimeout(resolve, 1000))
            continue
          }
        }
        // 其他错误或已达最大重试次数，不再重试
        break
      }
    }
    throw lastError
  }
}

/**
 * 上传图片
 * @param {File} file 图片文件
 * @param {Function} onProgress 进度回调函数
 * @returns {Promise} 返回Promise对象
 */
export const uploadImage = withRetry((file, onProgress) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', 'image')
  
  return createUploadInstance().post('', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress ? progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      onProgress(percentCompleted)
    } : undefined
  })
})

/**
 * 上传文档
 * @param {File} file 文档文件
 * @param {Function} onProgress 进度回调函数
 * @returns {Promise} 返回Promise对象
 */
export const uploadDocument = withRetry((file, onProgress) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', 'document')
  
  return createUploadInstance().post('', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress ? progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      onProgress(percentCompleted)
    } : undefined
  })
})

/**
 * 上传视频
 * @param {File} file 视频文件
 * @param {Function} onProgress 进度回调函数
 * @returns {Promise} 返回Promise对象
 */
export const uploadVideo = withRetry((file, onProgress) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', 'video')
  
  return createUploadInstance().post('', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress ? progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      onProgress(percentCompleted)
    } : undefined
  })
})

/**
 * 上传音频
 * @param {File} file 音频文件
 * @param {Function} onProgress 进度回调函数
 * @returns {Promise} 返回Promise对象
 */
export const uploadAudio = withRetry((file, onProgress) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', 'audio')
  
  return createUploadInstance().post('', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress ? progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      onProgress(percentCompleted)
    } : undefined
  })
})

/**
 * 上传通用文件
 * @param {File} file 文件
 * @param {Function} onProgress 进度回调函数
 * @returns {Promise} 返回Promise对象
 */
export const uploadFile = withRetry((file, onProgress) => {
  const formData = new FormData()
  formData.append('file', file)
  
  // 获取文件类型
  const fileType = file.name.split('.').pop().toLowerCase()
  formData.append('fileExt', fileType)
  
  return createUploadInstance().post('', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress ? progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      onProgress(percentCompleted)
    } : undefined
  })
})

/**
 * 获取文件URL
 * @param {String} fileId 文件ID或完整URL
 * @returns {String} 完整的文件URL
 */
export function getFileUrl(fileId) {
  if (!fileId) return '';
  
  // 如果已经是完整URL，直接返回
  if (fileId.startsWith('http://') || fileId.startsWith('https://')) {
    return fileId;
  }
  
  // 从云服务器获取文件
  return `http://110.41.15.50/api/file/${fileId}`;
} 