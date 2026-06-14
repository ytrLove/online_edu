import request from '@/utils/request'

/**
 * 上传文件
 * @param {File} file 文件对象
 * @returns {Promise} 返回Promise对象
 */
export const uploadFile  = (file, onProgress) => {
    return request({
        url: '/api/file/upload',
        method: 'post',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data: {
            file: file
        },
        onUploadProgress: onProgress ? progressEvent => {
            const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
            onProgress(percentCompleted)
        } : undefined

    })
}


export const deleteFile = (url) => {
  return request({
    url: "/api/file/delete",
    method: 'post',
    data: {
        url: url
    }
  })
}


export const getFileUrl = (fileId) =>{
    if (!fileId) return '';
    
    // 如果已经是完整URL，直接返回
    if (fileId.startsWith('http://') || fileId.startsWith('https://')) {
      return fileId;
    }
}