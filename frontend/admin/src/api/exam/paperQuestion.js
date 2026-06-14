import request from '@/utils/request'

/**
 * 获取试卷题目列表
 * @param {number} examId 试卷ID
 */
export function getExamPaperQuestions(examId) {
  return request({
    url: `/api/exam/paper/${examId}/questions`,
    method: 'get'
  })
}

/**
 * 添加/更新试卷题目
 * @param {Array} data 题目数据数组，每项格式：{examId, questionId, questionOrder, points}
 */
export function addExamPaperQuestions(data) {
  return request({
    url: '/api/exam/paper/question/batch',
    method: 'post',
    data
  })
}

/**
 * 删除试卷题目
 * @param {number} examId 试卷ID
 * @param {number} questionId 题目ID
 */
export function deleteExamPaperQuestion(examId, questionId) {
  return request({
    url: `/api/exam/paper/${examId}/question/${questionId}`,
    method: 'delete'
  })
} 