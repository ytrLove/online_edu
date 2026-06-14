import request from '@/utils/request'

// 获取作业提交列表
export function getSubmissionList(params) {
  return request({
    url: '/api/exam/submission/list',
    method: 'get',
    params
  })
}

// 获取作业提交详情
export function getSubmissionInfo(submissionId) {
  return request({
    url: `/api/exam/submission/${submissionId}`,
    method: 'get'
  })
}



// 学生提交作业
export function submitAssignment(data) {
  return request({
    url: '/api/exam/submission/submit',
    method: 'post',
    data
  })
}

// 教师批改作业
export function gradeSubmission(data) {
  return request({
    url: '/api/exam/submission/grade',
    method: 'put',
    data
  })
}

// 删除作业提交
export function deleteSubmission(submissionIds) {
  return request({
    url: `/api/exam/submission/${submissionIds}`,
    method: 'delete'
  })
}

// 导出作业提交数据
export function exportSubmissionData(params) {
  return request({
    url: '/api/exam/submission/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
} 