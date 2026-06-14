import request from '@/utils/request'

// 获取教师评价列表
export function getTeacherEvaluationList(query) {
  return request({
    url: '/api/stat/evaluation/list',
    method: 'get',
    params: query
  })
}

// 获取评价详情
export function getEvaluationDetail(evaluationId) {
  return request({
    url: `/api/stat/evaluation/${evaluationId}`,
    method: 'get'
  })
}

// 获取课程评价统计
export function getCourseEvaluationStats(courseId) {
  return request({
    url: `/api/stat/evaluation/course/${courseId}`,
    method: 'get'
  })
}

// 获取课程评分趋势
export function getCourseScoreTrend(courseId, query) {
  return request({
    url: `/api/stat/evaluation/course/${courseId}/trend`,
    method: 'get',
    params: query
  })
}

// 获取教师评分统计
export function getTeacherScoreStats(courseId) {
  return request({
    url: `/api/stat/evaluation/course/${courseId}/teacher`,
    method: 'get'
  })
}

// 提交教学评价
export function submitEvaluation(data) {
  return request({
    url: '/api/stat/evaluation',
    method: 'post',
    data: data
  })
}

// 导出评价数据
export function exportEvaluationData(query) {
  return request({
    url: '/api/stat/evaluation/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
} 