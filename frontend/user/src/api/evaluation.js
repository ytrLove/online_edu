import request from './request'

// 提交教学评价
export function submitEvaluation(data) {
  return request({
    url: '/api/stat/evaluation',
    method: 'post',
    data
  })
}

// 获取课程评价统计
export function getCourseEvaluationStats(courseId) {
  return request({
    url: `/api/stat/evaluation/course/${courseId}`,
    method: 'get'
  })
}

// 获取教师评分统计
export function getTeacherScoreStats(courseId) {
  return request({
    url: `/api/stat/evaluation/course/${courseId}/teacher`,
    method: 'get'
  })
}

// 获取课程评分趋势
export function getCourseScoreTrend(courseId, params) {
  return request({
    url: `/api/stat/evaluation/course/${courseId}/trend`,
    method: 'get',
    params
  })
}

// 获取课程评价列表
export function getEvaluationList(params) {
  return request({
    url: '/api/edu/evaluation/list',
    method: 'get',
    params
  })
} 