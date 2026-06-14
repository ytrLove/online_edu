import request from '@/utils/request'

// 获取定时任务列表
export function getScheduledTaskList(params) {
  return request({
    url: '/api/scheduled-tasks',
    method: 'get',
    params
  })
}

// 添加定时任务
export function addScheduledTask(data) {
  return request({
    url: '/api/scheduled-tasks',
    method: 'post',
    data
  })
}

// 更新定时任务
export function updateScheduledTask(id, data) {
  return request({
    url: `/api/scheduled-tasks/${id}`,
    method: 'put',
    data
  })
}

// 删除定时任务
export function deleteScheduledTask(id) {
  return request({
    url: `/api/scheduled-tasks/${id}`,
    method: 'delete'
  })
}

// 获取任务执行日志
export function getTaskLogs(id) {
  return request({
    url: `/api/scheduled-tasks/${id}/logs`,
    method: 'get'
  })
}

// 立即执行任务
export function executeTaskNow(id) {
  return request({
    url: `/api/scheduled-tasks/${id}/execute`,
    method: 'post'
  })
}

// 更改任务状态
export function changeTaskStatus(id, status) {
  return request({
    url: `/api/scheduled-tasks/${id}/status`,
    method: 'put',
    data: { status }
  })
}