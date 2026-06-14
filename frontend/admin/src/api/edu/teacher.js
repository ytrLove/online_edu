import request from '@/utils/request'

/**
 * 获取教师列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getTeacherList(params) {
  return request({
    url: '/api/edu/teacher/list',
    method: 'get',
    params
  })
}

/**
 * 获取教师详情
 * @param {String} id 教师ID
 * @returns {Promise} 返回Promise对象
 */
export function getTeacherById(id) {
  return request({
    url: `/api/edu/teacher/${id}`,
    method: 'get'
  })
}

/**
 * 新增教师
 * @param {Object} data 教师数据
 * @returns {Promise} 返回Promise对象
 */
export function addTeacher(data) {
  return request({
    url: '/api/edu/teacher',
    method: 'post',
    data
  })
}

/**
 * 更新教师
 * @param {Object} data 教师数据
 * @returns {Promise} 返回Promise对象
 */
export function updateTeacher(data) {
  return request({
    url: '/api/edu/teacher',
    method: 'put',
    data
  })
}

/**
 * 删除教师
 * @param {String} id 教师ID
 * @returns {Promise} 返回Promise对象
 */
export function deleteTeacher(id) {
  return request({
    url: `/api/edu/teacher/${id}`,
    method: 'delete'
  })
}

/**
 * 获取教师课程列表
 * @param {String} id 教师ID
 * @returns {Promise} 返回Promise对象
 */
export function getTeacherCourses(id) {
  return request({
    url: `/api/edu/teacher/${id}/courses`,
    method: 'get'
  })
} 