import request from '@/utils/request'

// 获取学习总结列表
export function getSummaryList(query) {
  return request({
    url: '/api/stat/summary/list',
    method: 'get',
    params: query
  })
}

// 获取总结详情
export function getSummaryDetail(summaryId) {
  return request({
    url: `/api/stat/summary/${summaryId}`,
    method: 'get'
  })
}

// 生成总结
export function generateSummary(data) {
  return request({
    url: '/api/stat/summary/generate',
    method: 'post',
    data: data
  })
}

// 导出总结
export function exportSummary(summaryId) {
  return request({
    url: `/api/stat/summary/${summaryId}/export`,
    method: 'get',
    responseType: 'blob'
  })
}

// 发送总结
export function sendSummary(summaryId) {
  return request({
    url: `/api/stat/summary/${summaryId}/send`,
    method: 'post'
  })
}

// 删除总结
export function deleteSummary(summaryId) {
  return request({
    url: `/api/stat/summary/${summaryId}`,
    method: 'delete'
  })
}

// 批量生成总结
export function batchGenerateSummary(data) {
  return request({
    url: '/api/stat/summary/batch-generate',
    method: 'post',
    data: data
  })
}

// 批量发送总结
export function batchSendSummary(data) {
  return request({
    url: '/api/stat/summary/batch-send',
    method: 'post',
    data: data
  })
}