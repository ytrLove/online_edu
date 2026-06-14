import request from '@/utils/request'

/**
 * 获取登录日志列表
 * @param {Object} params 查询参数对象
 */
export function getLoginLogList(params) {
  return request({
    url: '/api/login-logs',
    method: 'get',
    params
  })
}
