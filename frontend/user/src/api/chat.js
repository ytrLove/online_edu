import request from './request'

/**
 * 创建聊天会话ID
 * @returns {Promise<string>} 会话ID
 */
export function createChatSession() {
  return request({
    url: '/create/session_id',
    method: 'get'
  })
}

/**
 * 发送聊天消息并获取流式响应
 * @param {Object} data 请求数据
 * @param {string} data.query 用户输入的消息
 * @param {string} data.sessionId 会话ID
 * @returns {Promise<ReadableStream>} 流式响应
 */
export function sendChatMessage(data) {
  return request({
    url: '/chat',
    method: 'post',
    data
  })
}

/**
 * 获取聊天历史记录
 * @param {string} sessionId 会话ID
 * @returns {Promise<Array>} 聊天历史记录
 */
export function getChatHistory(sessionId) {
  return request({
    url: '/chat/history',
    method: 'get',
    params: { sessionId }
  })
}


/**
 * 获取所有聊天会话
 * @returns {Promise<Array>} 所有聊天会话
 */

export function getAllChatSessions() {
  return request({
    url: '/chat/sessions',
    method: 'get'
  })
}

/**
 * 删除聊天会话
 * @param {string} sessionId 会话ID
 * @returns {Promise<void>} 无返回值
 */

export function deleteChatSession(sessionId) {
  return request({
    url: '/chat/clear',
    method: 'delete',
    params: { sessionId }
  })
}
