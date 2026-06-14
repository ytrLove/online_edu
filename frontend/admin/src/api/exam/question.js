import request from '@/utils/request'

// 获取题目列表
export function getQuestionList(params) {
  return request({
    url: '/api/exam/question/list',
    method: 'get',
    params
  })
}

// 获取题目详情
export function getQuestionInfo(questionId) {
  return request({
    url: `/api/exam/question/${questionId}`,
    method: 'get'
  })
}

// 新增题目
export function addQuestion(data) {
  return request({
    url: '/api/exam/question',
    method: 'post',
    data
  })
}

// 修改题目
export function updateQuestion(data) {
  return request({
    url: '/api/exam/question',
    method: 'put',
    data
  })
}

// 删除题目
export function deleteQuestion(questionIds) {
  return request({
    url: `/api/exam/question/${questionIds}`,
    method: 'delete'
  })
} 