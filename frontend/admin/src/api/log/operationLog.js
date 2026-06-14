import request from '@/utils/request'

/**
 * 获取操作日志列表
 * @param {Object} params 查询参数
 */
export function getOperationLogList(params) {
  return request({
    url: '/api/modules-logs',
    method: 'get',
    params
  })
}

/**
 * 获取操作日志详情
 * @param {number} id 日志ID
 */
export function getOperationLogDetail(id) {
  return request({
    url: `/api/operation-logs/${id}`,
    method: 'get'
  })
}

/**
 * 导出操作日志
 * @param {Object} params 导出参数
 */
export function exportOperationLog(params) {
  return request({
    url: '/api/system/operation-logs/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 批量删除操作日志
 * @param {Array} ids 日志ID数组
 */
export function deleteOperationLogs(ids) {
  return request({
    url: '/api/system/operation-logs/batch-delete',
    method: 'delete',
    data: ids
  })
}