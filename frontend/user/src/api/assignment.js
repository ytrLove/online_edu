import request from './request'

// 保留旧函数名称以兼容现有代码
export function getAssignmentList(params) {
  return request({
    url: '/api/edu/student/assignments',
    method: 'get',
    params
  })
}

// 获取学生的作业列表
export function getStudentAssignments() {
  return request({
    url: '/api/edu/student/assignments',
    method: 'get'
  })
}

// 获取学生的作业详情
export function getAssignmentDetail(assignmentId) {
  return request({
    url: `/api/edu/student/assignment/${assignmentId}`,
    method: 'get'
  })
}

// 提交作业
export function submitAssignment(data) {
  return request({
    url: '/api/edu/student/assignment/submit',
    method: 'post',
    data
  })
}

// 查看作业提交详情
export function getSubmissionDetail(submissionId) {
  return request({
    url: `/api/edu/student/assignment/submission/${submissionId}`,
    method: 'get'
  })
}

// 获取学生的考试列表
export function getStudentExams(params) {
  return request({
    url: '/api/edu/student/exams',
    method: 'get',
    params
  })
}

// 获取学生的考试详情
export function getExamDetail(examId) {
  return request({
    url: `/api/edu/student/exam/${examId}`,
    method: 'get'
  })
}

// 开始考试
export function startExam(examId) {
  return request({
    url: `/api/edu/student/exam/${examId}/start`,
    method: 'post'
  })
}

// 提交考试
export function submitExam(data) {
  return request({
    url: '/api/edu/student/exam/submit',
    method: 'post',
    data
  })
}

// 获取考试记录列表
export function getExamRecords(params) {
  return request({
    url: '/api/edu/student/exam/records',
    method: 'get',
    params
  })
}

// 获取考试记录详情
export function getExamRecordDetail(recordId) {
  return request({
    url: `/api/edu/student/exam/record/${recordId}`,
    method: 'get'
  })
}

// 获取学生的课程列表
export function getStudentCourses() {
  return request({
    url: '/api/edu/student/courses',
    method: 'get'
  })
} 