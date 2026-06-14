import request from '@/utils/request'

// 获取教师报告分页
export function getTeacherReportPage(query) {
  return request({
    url: '/api/stat/report/teacher',
    method: 'get',
    params: query
  })
}

// 获取课程报告分页
export function getCourseReportPage(query) {
  return request({
    url: '/api/stat/report/course',
    method: 'get',
    params: query
  })
}

// 获取报告详情
export function getReportDetail(reportId) {
  return request({
    url: `/api/stat/report/${reportId}`,
    method: 'get'
  })
}

// 生成报告
export function generateReport(data) {
  return request({
    url: '/api/stat/report/generate',
    method: 'post',
    data: data
  })
}

// 导出报告
export function exportReport(reportId) {
  return request({
    url: `/api/stat/report/${reportId}/export`,
    method: 'get',
    responseType: 'blob'
  })
}

// 删除报告
export function deleteReport(reportId) {
  return request({
    url: `/api/stat/report/${reportId}`,
    method: 'delete'
  })
}