import request from '@/utils/request'

// 获取试卷列表
export function getExamPaperList(params) {
  return request({
    url: '/api/exam/paper/list',
    method: 'get',
    params
  })
}

// 获取试卷详情
export function getExamPaperInfo(examId) {
  return request({
    url: `/api/exam/paper/${examId}`,
    method: 'get'
  })
}

// 学生获取试卷列表
export function getStudentExamPaperList(params) {
  return request({
    url: '/api/exam/paper/student/list',
    method: 'get',
    params
  })
}

// 学生获取试卷详情
export function getStudentExamPaperInfo(examId) {
  return request({
    url: `/api/exam/paper/student/${examId}`,
    method: 'get'
  })
}

// 新增试卷
export function addExamPaper(data) {
  return request({
    url: '/api/exam/paper',
    method: 'post',
    data
  })
}

// 修改试卷
export function updateExamPaper(data) {
  return request({
    url: '/api/exam/paper',
    method: 'put',
    data
  })
}

// 删除试卷
export function deleteExamPaper(examIds) {
  return request({
    url: `/api/exam/paper/${examIds}`,
    method: 'delete'
  })
}

// 发布试卷
export function publishExamPaper(examId) {
  return request({
    url: `/api/exam/paper/publish/${examId}`,
    method: 'put'
  })
}

// 下架试卷
export function unpublishExamPaper(examId) {
  return request({
    url: `/api/exam/paper/unpublish/${examId}`,
    method: 'put'
  })
} 