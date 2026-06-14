import request from './request'

// 获取课程资源列表
export function getCourseResources(courseId) {
  return request({
    url: `/api/edu/resource/course/${courseId}`,
    method: 'get'
  })
}

// 预览资源
export function previewResource(resourceId) {
  return request({
    url: `/api/edu/resource/preview/${resourceId}`,
    method: 'get'
  })
}

// 下载资源
export function downloadResource(resourceId) {
  return request({
    url: `/api/edu/resource/download/${resourceId}`,
    method: 'get'
  })
}

// 记录学习行为
export function recordBehavior(data) {
  return request({
    url: '/stat/behavior',
    method: 'post',
    data
  })
} 