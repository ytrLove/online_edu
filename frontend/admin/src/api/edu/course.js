import request from '@/utils/request'
import { uploadImage } from '@/api/common/upload'

/**
 * 获取课程列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getCourseList(params) {
  // 转换为适配CourseQueryRequest的参数
  const queryParams = {
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 10,
    courseName: params.keyword,
    subjectId: params.subjectId,
    status: params.status,
    teacherId: params.teacherId
  };
  
  return request({
    url: '/api/edu/course/list',
    method: 'get',
    params: queryParams
  })
}

/**
 * 获取课程选项列表（用于下拉框）
 * @returns {Promise} 返回Promise对象
 */
export function getCourseOptions() {
  return request({
    url: '/api/edu/course/options',
    method: 'get'
  })
}

/**
 * 获取课程详情
 * @param {String} id 课程ID
 * @returns {Promise} 返回Promise对象
 */
export function getCourseById(id) {
  return request({
    url: `/api/edu/course/${id}`,
    method: 'get'
  })
}

/**
 * 新增课程
 * @param {Object} data 课程数据
 * @returns {Promise} 返回Promise对象
 */
export function addCourse(data) {
  // 转换为适配CourseCreateRequest的参数
  const createParams = {
    courseName: data.courseName,
    courseCode: data.courseCode,
    subjectId: data.subjectId,
    description: data.description,
    objectives: data.objectives,
    syllabus: data.syllabus,
    coverImage: data.coverImage,
    status: data.status,
    enrollmentStartTime: data.enrollmentStartTime,
    enrollmentEndTime: data.enrollmentEndTime,
    enrollmentStatus: data.enrollmentStatus,
    teacherIds: data.teacherIds || [],
    remark: data.remark
  };
  
  return request({
    url: '/api/edu/course',
    method: 'post',
    data: createParams
  })
}

/**
 * 更新课程
 * @param {Object} data 课程数据
 * @returns {Promise} 返回Promise对象
 */
export function updateCourse(data) {
  // 转换为适配CourseUpdateRequest的参数
  const updateParams = {
    courseId: data.courseId,
    courseName: data.courseName,
    courseCode: data.courseCode,
    subjectId: data.subjectId,
    description: data.description,
    objectives: data.objectives,
    syllabus: data.syllabus,
    coverImage: data.coverImage,
    status: data.status,
    enrollmentStartTime: data.enrollmentStartTime,
    enrollmentEndTime: data.enrollmentEndTime,
    enrollmentStatus: data.enrollmentStatus,
    teacherIds: data.teacherIds || [],
    remark: data.remark
  };
  
  return request({
    url: '/api/edu/course',
    method: 'put',
    data: updateParams
  })
}

/**
 * 删除课程
 * @param {String} id 课程ID
 * @returns {Promise} 返回Promise对象
 */
export function deleteCourse(id) {
  return request({
    url: `/api/edu/course/${id}`,
    method: 'delete'
  })
}

/**
 * 获取课程学生列表
 * @param {String} id 课程ID
 * @returns {Promise} 返回Promise对象
 */
export function getCourseStudents(id) {
  return request({
    url: `/api/edu/course/${id}/students`,
    method: 'get'
  })
}

/**
 * 上传课程封面
 * @param {File} file 封面图片文件
 * @returns {Promise} 返回Promise对象
 */
export function uploadCourseCover(file) {
  return uploadImage(file)
}

/**
 * 获取特定分类下的课程列表
 * @param {Object} params 查询参数，包含subjectId
 * @returns {Promise} 返回Promise对象
 */
export function getCoursesBySubject(params) {
  // 转换为适配CourseQueryRequest的参数
  const queryParams = {
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 10,
    subjectId: params.subjectId,
    status: 'published'
  };
  
  return request({
    url: '/api/edu/course/list',
    method: 'get',
    params: queryParams
  })
}

/**
 * 获取当前教师的课程列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getTeacherCourseList(params) {
  // 转换为适配CourseQueryRequest的参数
  const queryParams = {
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 10,
    courseName: params.keyword,
    status: params.status,
    subjectId: params.subjectId
  };
  
  return request({
    url: '/api/edu/course/current/list',
    method: 'get',
    params: queryParams
  })
} 