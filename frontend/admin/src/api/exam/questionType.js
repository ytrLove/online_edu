import request from '@/utils/request'

// 获取题目类型列表（分页）
export function getQuestionTypeList(params) {
  return request({
    url: '/api/exam/questionType/list',
    method: 'get',
    params
  })
}

// 获取所有题目类型（不分页）
export function getAllQuestionTypes() {
  return request({
    url: '/api/exam/questionType/listAll',
    method: 'get'
  })
}

// 获取题目类型详情
export function getQuestionTypeInfo(typeId) {
  return request({
    url: `/api/exam/questionType/${typeId}`,
    method: 'get'
  })
}

// 新增题目类型
export function addQuestionType(data) {
  return request({
    url: '/api/exam/questionType',
    method: 'post',
    data
  })
}

// 修改题目类型
export function updateQuestionType(data) {
  return request({
    url: '/api/exam/questionType',
    method: 'put',
    data
  })
}

// 删除题目类型
export function deleteQuestionType(typeIds) {
  return request({
    url: `/api/exam/questionType/${typeIds}`,
    method: 'delete'
  })
} 