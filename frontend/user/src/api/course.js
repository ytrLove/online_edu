import request from './request'

// 获取课程列表
export function getCourseList(params) {
  return request({
    url: '/api/edu/course/list',
    method: 'get',
    params
  })
}

// 获取课程详情
export function getCourseDetail(courseId) {
  return request({
    url: `/api/edu/course/${courseId}`,
    method: 'get'
  })
}

// 选课/加入课程
export function enrollCourse(data) {
  return request({
    url: `/api/edu/course/enroll/${data.courseId}`,
    method: 'get',
  })
}

// 获取已选课程列表
export function getEnrolledCourses() {
  return request({
    url: `/api/edu/student/courses`,
    method: 'get'
  })
}

// 获取课程科目列表
export function getSubjectList() {
  return request({
    url: '/api/edu/subject/list',
    method: 'get'
  })
} 