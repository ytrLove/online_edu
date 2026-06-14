import request from '@/utils/request'

/**
 * 获取资源列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回获取资源列表的Promise对象
 */
export function getResourceList(params) {
  return request({
    url: '/api/edu/resource/list',
    method: 'get',
    params
  })
}

/**
 * 获取资源详情
 * @param {String} id - 资源ID
 * @returns {Promise} - 返回获取资源详情的Promise对象
 */
export function getResourceById(id) {
  return request({
    url: `/api/edu/resource/${id}`,
    method: 'get'
  })
}

/**
 * 新增资源
 * @param {Object} data - 资源数据
 * @returns {Promise} - 返回新增资源的Promise对象
 */
export function addResource(data) {
  return request({
    url: '/api/edu/resource',
    method: 'post',
    data
  })
}

/**
 * 修改资源
 * @param {Object} data - 资源数据
 * @returns {Promise} - 返回修改资源的Promise对象
 */
export function updateResource(data) {
  return request({
    url: '/api/edu/resource',
    method: 'put',
    data
  })
}

/**
 * 删除资源
 * @param {String} id - 资源ID
 * @returns {Promise} - 返回删除资源的Promise对象
 */
export function deleteResource(id) {
  return request({
    url: `/api/edu/resource/${id}`,
    method: 'delete'
  })
}

/**
 * 获取资源类型列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回获取资源类型列表的Promise对象
 */
export function getResourceTypeList(params) {
  return request({
    url: '/api/edu/resource/type/list',
    method: 'get',
    params
  })
}

/**
 * 获取资源类型详情
 * @param {String} id - 资源类型ID
 * @returns {Promise} - 返回获取资源类型详情的Promise对象
 */
export function getResourceTypeById(id) {
  return request({
    url: `/api/edu/resource/type/${id}`,
    method: 'get'
  })
}

/**
 * 新增资源类型
 * @param {Object} data - 资源类型数据
 * @returns {Promise} - 返回新增资源类型的Promise对象
 */
export function addResourceType(data) {
  return request({
    url: '/api/edu/resource/type',
    method: 'post',
    data
  })
}

/**
 * 修改资源类型
 * @param {Object} data - 资源类型数据
 * @returns {Promise} - 返回修改资源类型的Promise对象
 */
export function updateResourceType(data) {
  return request({
    url: '/api/edu/resource/type',
    method: 'put',
    data
  })
}

/**
 * 删除资源类型
 * @param {String} id - 资源类型ID
 * @returns {Promise} - 返回删除资源类型的Promise对象
 */
export function deleteResourceType(id) {
  return request({
    url: `/api/edu/resource/type/${id}`,
    method: 'delete'
  })
}

/**
 * 上传资源文件
 * @param {FormData} data - 包含文件的FormData对象
 * @returns {Promise} - 返回上传资源文件的Promise对象
 */
export function uploadResourceFile(data) {
  return request({
    url: '/api/edu/resource/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json' // 确保设置正确的Content-Type
    }
  })
}

// 为了兼容性，添加uploadResource别名
export const uploadResource = (data) => {
  // 使用JSON格式提交数据
  return request({
    url: '/api/edu/resource/upload', // 修改为正确的URL路径
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json' // 明确设置为JSON格式
    }
  })
};

/**
 * 下载资源文件
 * @param {String} id - 资源ID
 * @returns {Promise} - 返回下载资源文件的Promise对象
 */
export function downloadResourceFile(id) {
  return request({
    url: `/api/edu/resource/download/${id}`,
    method: 'get',
  })
}

// 为了兼容性，添加downloadResource别名
export const downloadResource = downloadResourceFile;

/**
 * 获取课程资源列表
 * @param {Object} params - 包含courseId的参数对象
 * @returns {Promise} - 返回获取课程资源列表的Promise对象
 */
export function getCourseResourceList(params) {
  // 从params中提取courseId
  const courseId = params.courseId;
  if (!courseId) {
    return Promise.reject(new Error('课程ID不能为空'));
  }
  
  return request({
    url: `/api/edu/resource/course/${courseId}`,
    method: 'get'
    // 注意：此API不需要传递参数，因为courseId已经在URL中
  });
}

/**
 * 获取我的资源列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回获取我的资源列表的Promise对象
 */
export function getMyResourceList(params) {
  return request({
    url: '/api/edu/resource/my/list',
    method: 'get',
    params
  })
}

/**
 * 获取共享资源列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getSharedResourceList(params) {
  return request({
    url: '/api/edu/resource/shared/list',
    method: 'get',
    params
  })
}

/**
 * 共享资源
 * @param {String} id 资源ID
 * @returns {Promise} 返回Promise对象
 */
export function shareResource(id) {
  return request({
    url: `/api/edu/resource/${id}/share`,
    method: 'put'
  })
}

/**
 * 取消共享资源
 * @param {String} id 资源ID
 * @returns {Promise} 返回Promise对象
 */
export function cancelShareResource(id) {
  return request({
    url: `/api/edu/resource/${id}/cancelShare`,
    method: 'put'
  })
}

/**
 * 审核资源
 * @param {String} id 资源ID
 * @param {Object} data 审核数据
 * @returns {Promise} 返回Promise对象
 */
export function reviewResource(id, data) {
  return request({
    url: `/api/edu/resource/${id}/review`,
    method: 'put',
    data
  })
}

/**
 * 获取待审核资源列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getPendingReviewResourceList(params) {
  return request({
    url: '/api/edu/resource/pending-review/list',
    method: 'get',
    params
  })
}

/**
 * 获取所有启用的资源类型
 * @returns {Promise} - 返回获取所有启用的资源类型的Promise对象
 */
export function getAllEnabledResourceTypes() {
  return request({
    url: '/api/edu/resource/type/all',
    method: 'get'
  });
} 