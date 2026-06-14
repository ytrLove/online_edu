import request from '@/utils/request'

// 获取作业列表
export function getAssignmentList(params) {
  return request({
    url: '/api/exam/assignment/list',
    method: 'get',
    params
  })
}

// 获取作业详情
export function getAssignmentInfo(assignmentId) {
  return request({
    url: `/api/exam/assignment/${assignmentId}`,
    method: 'get'
  })
}

// 学生获取作业列表
export function getStudentAssignmentList(params) {
  return request({
    url: '/api/exam/assignment/student/list',
    method: 'get',
    params
  })
}

// 学生获取作业详情
export function getStudentAssignmentInfo(assignmentId) {
  return request({
    url: `/api/exam/assignment/student/${assignmentId}`,
    method: 'get'
  })
}

// 新增作业
export function addAssignment(data) {
  return request({
    url: '/api/exam/assignment',
    method: 'post',
    data
  })
}

// 修改作业
export function updateAssignment(data) {
  return request({
    url: '/api/exam/assignment',
    method: 'put',
    data
  })
}

// 删除作业
export function deleteAssignment(assignmentIds) {
  return request({
    url: `/api/exam/assignment/${assignmentIds}`,
    method: 'delete'
  })
}

// 关联题目到作业
export function associateQuestions(data) {
  return request({
    url: '/api/exam/assignment/associate-questions',
    method: 'post',
    data
  })
} 