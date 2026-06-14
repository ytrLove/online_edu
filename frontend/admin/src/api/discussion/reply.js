import request from '@/utils/request';

// 查询讨论回复列表
export function listReply(topicId, query) {
  return request({
    url: '/api/discussion/reply/list/' + topicId,
    method: 'get',
    params: query
  });
}

// 新增讨论回复
export function createReply(data) {
  return request({
    url: '/api/discussion/reply',
    method: 'post',
    data: data
  });
}

// 修改讨论回复
export function updateReply(data) {
  return request({
    url: '/api/discussion/reply',
    method: 'put',
    data: data
  });
}

// 删除讨论回复
export function deleteReply(replyId) {
  return request({
    url: '/api/discussion/reply/' + replyId,
    method: 'delete'
  });
} 