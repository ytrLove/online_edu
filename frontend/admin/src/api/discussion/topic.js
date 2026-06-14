import request from '@/utils/request';

// 查询讨论主题列表
export function listTopic(query) {
  return request({
    url: '/api/discussion/topic/list',
    method: 'get',
    params: query
  });
}

// 查询讨论主题详细
export function getTopic(topicId) {
  return request({
    url: '/api/discussion/topic/' + topicId,
    method: 'get'
  });
}

// 新增讨论主题
export function createTopic(data) {
  return request({
    url: '/api/discussion/topic',
    method: 'post',
    data: data
  });
}

// 修改讨论主题
export function updateTopic(data) {
  return request({
    url: '/api/discussion/topic',
    method: 'put',
    data: data
  });
}

// 删除讨论主题
export function deleteTopic(topicId) {
  return request({
    url: '/api/discussion/topic/' + topicId,
    method: 'delete'
  });
} 