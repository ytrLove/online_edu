import request from '@/utils/request';

/**
 * 获取课程的讨论区列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getForumList(params) {
  return request({
    url: '/api/discussion/forum/list',
    method: 'get',
    params
  });
}

/**
 * 获取讨论区详情
 * @param {number} forumId - 讨论区ID
 * @returns {Promise}
 */
export function getForumDetail(forumId) {
  return request({
    url: `/api/discussion/forum/${forumId}`,
    method: 'get'
  });
}

/**
 * 创建讨论区
 * @param {Object} data - 讨论区数据
 * @returns {Promise}
 */
export function createForum(data) {
  return request({
    url: '/api/discussion/forum',
    method: 'post',
    data
  });
}

/**
 * 更新讨论区
 * @param {Object} data - 讨论区数据
 * @returns {Promise}
 */
export function updateForum(data) {
  return request({
    url: '/api/discussion/forum',
    method: 'put',
    data
  });
}

/**
 * 删除讨论区
 * @param {number} forumId - 讨论区ID
 * @returns {Promise}
 */
export function deleteForum(forumId) {
  return request({
    url: `/api/discussion/forum/${forumId}`,
    method: 'delete'
  });
}

/**
 * 获取讨论区主题列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getTopicList(params) {
  return request({
    url: '/api/discussion/topic/list',
    method: 'get',
    params
  });
}

/**
 * 获取主题详情
 * @param {number} topicId - 主题ID
 * @returns {Promise}
 */
export function getTopicDetail(topicId) {
  return request({
    url: `/api/discussion/topic/${topicId}`,
    method: 'get'
  });
}

/**
 * 创建主题
 * @param {Object} data - 主题数据
 * @returns {Promise}
 */
export function createTopic(data) {
  return request({
    url: '/api/discussion/topic',
    method: 'post',
    data
  });
}

/**
 * 更新主题
 * @param {Object} data - 主题数据
 * @returns {Promise}
 */
export function updateTopic(data) {
  return request({
    url: '/api/discussion/topic',
    method: 'put',
    data
  });
}

/**
 * 删除主题
 * @param {number} topicId - 主题ID
 * @returns {Promise}
 */
export function deleteTopic(topicId) {
  return request({
    url: `/api/discussion/topic/${topicId}`,
    method: 'delete'
  });
}

/**
 * 获取主题回复列表
 * @param {number} topicId - 主题ID
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getReplyList(topicId, params) {
  return request({
    url: `/api/discussion/reply/list/${topicId}`,
    method: 'get',
    params
  });
}

/**
 * 创建回复
 * @param {Object} data - 回复数据
 * @returns {Promise}
 */
export function createReply(data) {
  return request({
    url: '/api/discussion/reply',
    method: 'post',
    data
  });
}

/**
 * 更新回复
 * @param {Object} data - 回复数据
 * @returns {Promise}
 */
export function updateReply(data) {
  return request({
    url: '/api/discussion/reply',
    method: 'put',
    data
  });
}

/**
 * 删除回复
 * @param {number} replyId - 回复ID
 * @returns {Promise}
 */
export function deleteReply(replyId) {
  return request({
    url: `/api/discussion/reply/${replyId}`,
    method: 'delete'
  });
} 