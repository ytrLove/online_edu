import request from './request'

// 记录学习行为
export function recordBehavior(data) {
  return request({
    url: '/api/stat/behavior',
    method: 'post',
    data: data
  })
}

// 批量记录学习行为
export function recordBehaviors(data) {
  return request({
    url: '/api/stat/behavior/batch',
    method: 'post',
    data: data
  })
}

// 查询学习行为数据
export function getBehaviorList(query) {
  return request({
    url: '/api/stat/behavior/list',
    method: 'get',
    params: query
  })
}

// 获取学生课程学习概要
export function getStudentCourseSummary(studentId, courseId) {
  return request({
    url: `/api/stat/summary/${studentId}/${courseId}`,
    method: 'get'
  })
}

// 获取学生所有课程学习概要
export function getStudentAllCourseSummary(studentId) {
  return request({
    url: `/api/stat/summary/student/${studentId}`,
    method: 'get'
  })
}