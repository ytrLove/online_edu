import request from '@/utils/request';

// 查询讨论区列表
export function listForum(query) {
  return request({
    url: '/api/discussion/forum/list',
    method: 'get',
    params: query
  });
}

// 查询讨论区详细
export function getForum(forumId) {
  return request({
    url: '/api/discussion/forum/' + forumId,
    method: 'get'
  });
}

// 新增讨论区
export function createForum(data) {
  return request({
    url: '/api/discussion/forum',
    method: 'post',
    data: data
  });
}

// 修改讨论区
export function updateForum(data) {
  return request({
    url: '/api/discussion/forum',
    method: 'put',
    data: data
  });
}

// 删除讨论区
export function deleteForum(forumId) {
  return request({
    url: '/api/discussion/forum/' + forumId,
    method: 'delete'
  });
} 