import request from './request'

// 获取首页数据
export function getCourseList() {
  return request({
    url: '/api/edu/student/courses',
    method: 'get'
  })
}

// 获取作业列表
export function getHomeworkList() {
  return request({
    url: '/api/edu/student//assignments',
    method: 'get'
  })
}
