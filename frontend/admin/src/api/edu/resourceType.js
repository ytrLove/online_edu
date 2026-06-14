import request from '@/utils/request'

/**
 * 获取资源类型列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
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
 * @param {String} id 资源类型ID
 * @returns {Promise} 返回Promise对象
 */
export function getResourceTypeById(id) {
  return request({
    url: `/api/edu/resource/type/${id}`,
    method: 'get'
  })
}

/**
 * 新增资源类型
 * @param {Object} data 资源类型数据
 * @returns {Promise} 返回Promise对象
 */
export function addResourceType(data) {
  return request({
    url: '/api/edu/resource/type',
    method: 'post',
    data
  })
}

/**
 * 更新资源类型
 * @param {Object} data 资源类型数据
 * @returns {Promise} 返回Promise对象
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
 * @param {String} id 资源类型ID
 * @returns {Promise} 返回Promise对象
 */
export function deleteResourceType(id) {
  return request({
    url: `/api/edu/resource/type/${id}`,
    method: 'delete'
  })
} 