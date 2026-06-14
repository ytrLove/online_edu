import request from '@/utils/request'

// 获取考试记录列表
export function getExamRecordList(params) {
  return request({
    url: '/api/exam/record/list',
    method: 'get',
    params
  })
}

// 获取考试记录详情
export function getExamRecordDetail(recordId) {
  return request({
    url: `/api/exam/record/${recordId}`,
    method: 'get'
  })
}

// 获取考试统计信息
export function getExamStats(examId) {
  return request({
    url: `/api/exam/record/stats/${examId}`,
    method: 'get'
  })
}

// 学生获取考试记录列表
export function getStudentExamRecordList(params) {
  return request({
    url: '/api/exam/record/student/list',
    method: 'get',
    params
  })
}

// 学生开始考试
export function startExam(examId) {
  return request({
    url: `/api/exam/record/start/${examId}`,
    method: 'post'
  })
}

// 学生提交考试
export function submitExam(data) {
  return request({
    url: '/api/exam/record/submit',
    method: 'post',
    data
  })
}

// 教师批改考试
export function gradeExam(data) {
  return request({
    url: '/api/exam/record/grade',
    method: 'put',
    data
  })
}

// 删除考试记录
export function deleteExamRecord(recordId) {
  return request({
    url: `/api/exam/record/${recordId}`,
    method: 'delete'
  })
}

// 获取考试答案
export function getExamAnswers(recordId) {
  return request({
    url: `/api/exam/record/${recordId}/answers`,
    method: 'get'
  })
} 