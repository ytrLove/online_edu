import request from '@/utils/request'

// 获取学习行为列表
export function getBehaviorList(query) {
  return request({
    url: '/api/stat/behavior/list',
    method: 'get',
    params: query
  })
}

// 获取学习行为统计
export function getBehaviorStats(query) {
  return request({
    url: '/api/stat/behavior/stats',
    method: 'get',
    params: query
  })
}

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

// 获取特定学生的行为分析
export function getStudentBehaviorAnalysis(studentId, query) {
  return request({
    url: `/api/stat/behavior/student/${studentId}/analysis`,
    method: 'get',
    params: query
  })
}

// 获取课程行为分析
export function getCourseBehaviorAnalysis(courseId, query) {
  return request({
    url: `/api/stat/behavior/course/${courseId}/analysis`,
    method: 'get',
    params: query
  })
}

// 导出行为数据
export function exportBehaviorData(query) {
  return request({
    url: '/api/stat/behavior/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}