import request from '@/utils/request'

/**
 * 获取课程分类列表
 * @returns {Promise} 返回Promise对象
 */
export function getSubjectList() {
  return request({
    url: '/api/edu/subject/list',
    method: 'get'
  })
}

/**
 * 获取课程分类详情
 * @param {String} id 分类ID
 * @returns {Promise} 返回Promise对象
 */
export function getSubjectById(id) {
  return request({
    url: `/api/edu/subject/${id}`,
    method: 'get'
  })
}

/**
 * 新增课程分类
 * @param {Object} data 分类数据
 * @returns {Promise} 返回Promise对象
 */
export function addSubject(data) {
  return request({
    url: '/api/edu/subject',
    method: 'post',
    data
  })
}

/**
 * 更新课程分类
 * @param {Object} data 分类数据
 * @returns {Promise} 返回Promise对象
 */
export function updateSubject(data) {
  return request({
    url: '/api/edu/subject',
    method: 'put',
    data
  })
}

/**
 * 删除课程分类
 * @param {String} id 分类ID
 * @returns {Promise} 返回Promise对象
 */
export function deleteSubject(id) {
  return request({
    url: `/api/edu/subject/${id}`,
    method: 'delete'
  })
} 