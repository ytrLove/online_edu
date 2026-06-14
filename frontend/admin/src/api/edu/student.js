import request from '@/utils/request'

/**
 * 获取学生列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getStudentList(params) {
  return request({
    url: '/api/edu/student/list',
    method: 'get',
    params
  })
}

/**
 * 获取学生详情
 * @param {String} id 学生ID
 * @returns {Promise} 返回Promise对象
 */
export function getStudentById(id) {
  return request({
    url: `/api/edu/student/${id}`,
    method: 'get'
  })
}

/**
 * 新增学生
 * @param {Object} data 学生数据
 * @returns {Promise} 返回Promise对象
 */
export function addStudent(data) {
  return request({
    url: '/api/edu/student',
    method: 'post',
    data
  })
}

/**
 * 更新学生
 * @param {Object} data 学生数据
 * @returns {Promise} 返回Promise对象
 */
export function updateStudent(data) {
  return request({
    url: '/api/edu/student',
    method: 'put',
    data
  })
}

/**
 * 删除学生
 * @param {String} id 学生ID
 * @returns {Promise} 返回Promise对象
 */
export function deleteStudent(id) {
  return request({
    url: `/api/edu/student/${id}`,
    method: 'delete'
  })
}

/**
 * 获取学生选课列表
 * @param {String} id 学生ID
 * @returns {Promise} 返回Promise对象
 */
export function getStudentCourses(id) {
  return request({
    url: `/api/edu/student/${id}/courses`,
    method: 'get'
  })
} 