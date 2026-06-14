import request from './request'

// 获取课程讨论区
export function getForumList(params) {
  return request({
    url: '/api/discussion/forum/list',
    method: 'get',
    params
  })
}

export function getForumById(forumId) {
  return request({
    url: `/api/discussion/forum/${forumId}`,
    method: 'get'
  })
}

// 获取讨论主题列表
export function getTopicList(params) {
  return request({
    url: '/api/discussion/topic/list',
    method: 'get',
    params
  })
}

export function getTopicById(topicId) {
  return request({
    url: `/api/discussion/topic/${topicId}`,
    method: 'get'
  })
}

// 获取讨论主题详情
export function getTopicDetail(topicId) {
  return request({
    url: `/api/discussion/topic/${topicId}`,
    method: 'get'
  })
}

// 发布讨论主题
export function createTopic(data) {
  return request({
    url: '/api/discussion/topic',
    method: 'post',
    data
  })
}

export function updateTopic(data) {
  return request({
    url: '/api/discussion/topic',
    method: 'put',
    data
  })
}

export function deleteTopic(topicId) {
  return request({
    url: `/api/discussion/topic/${topicId}`,
    method: 'delete'
  })
}

// 获取讨论回复列表
export function getReplyList(topicId, params) {
  return request({
    url: `/api/discussion/reply/list/${topicId}`,
    method: 'get',
    params
  })
}

// 发布讨论回复
export function createReply(data) {
  return request({
    url: '/api/discussion/reply',
    method: 'post',
    data
  })
}

export function updateReply(data) {
  return request({
    url: '/api/discussion/reply',
    method: 'put',
    data
  })
}

export function deleteReply(replyId) {
  return request({
    url: `/api/discussion/reply/${replyId}`,
    method: 'delete'
  })
} 