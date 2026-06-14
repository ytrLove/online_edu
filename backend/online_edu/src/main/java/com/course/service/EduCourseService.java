package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.model.dto.request.CourseCreateRequest;
import com.course.model.dto.request.CourseQueryRequest;
import com.course.model.dto.request.CourseUpdateRequest;
import com.course.model.dto.response.CourseDetailResponse;
import com.course.model.dto.response.CourseListResponse;
import com.course.model.entity.EduCourse;
import java.util.List;
import java.util.Map;

public interface EduCourseService extends IService<EduCourse> {
    /**
     * 分页查询课程列表
     * @param request 查询条件
     * @return 分页数据
     */
    IPage<CourseListResponse> listCourses(CourseQueryRequest request);
    
    /**
     * 获取课程详情
     * @param courseId
     * @return 课程详情响应
     */
    CourseDetailResponse getCourseDetailById(Long courseId);
    
    /**
     * 创建课程
     * @param request 课程创建请求
     * @return 创建的课程详情
     */
    CourseDetailResponse createCourse(CourseCreateRequest request);
    
    /**
     * 更新课程
     * @param request 课程更新请求
     * @return 更新后的课程详情
     */
    CourseDetailResponse updateCourse(CourseUpdateRequest request);
    
    /**
     * 删除课程
     * @param courseId 课程ID
     * @return 是否成功
     */
    boolean deleteCourse(Long courseId);
    
    /**
     * 更新课程状态
     * @param courseId 课程ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateCourseStatus(Long courseId, String status);
    
    /**
     * 获取课程学生列表
     * @param courseId 课程ID
     * @return 学生列表
     */
    List<Map<String, Object>> getCourseStudents(Long courseId);

    /**
     * 学生加入课程
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 是否成功
     */
    boolean enroll(Long courseId, Long studentId);
    
    /**
     * 获取学生已选课程数量
     */
    int getStudentCourseCount(Long userId);

    /**
     * 获取学生最近的课程（按最近学习时间排序）
     */
    IPage<CourseListResponse> getStudentRecentCourses(Long userId, Integer pageNum, Integer pageSize);
    
    // 保留旧方法以兼容现有代码，后续可逐步替换
    
    /**
     * 分页查询课程列表 (旧方法，将逐步替换)
     * @param course 查询条件
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页数据
     */
    @Deprecated
    IPage<EduCourse> listCourses(EduCourse course, Integer pageNum, Integer pageSize);
    
    /**
     * 获取课程详情 (旧方法，将逐步替换)
     * @param courseId 课程ID
     * @return 课程详情
     */
    @Deprecated
    EduCourse getCourseById(Long courseId);
    
    /**
     * 创建课程 (旧方法，将逐步替换)
     * @param course 课程信息
     * @param teacherIds 教师ID列表
     * @return 创建的课程
     */
    @Deprecated
    EduCourse createCourse(EduCourse course, List<Long> teacherIds);
    
    /**
     * 更新课程 (旧方法，将逐步替换)
     * @param course 课程信息
     * @param teacherIds 教师ID列表
     * @return 更新后的课程
     */
    @Deprecated
    EduCourse updateCourse(EduCourse course, List<Long> teacherIds);
    
    /**
     * 查询教师的课程列表 (旧方法，将逐步替换)
     * @param teacherId 教师ID
     * @return 课程列表
     */
    @Deprecated
    List<EduCourse> getCoursesByTeacherId(Long teacherId);
    
    /**
     * 分页查询教师的课程列表 (旧方法，将逐步替换)
     * @param teacherId 教师ID
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param course 查询条件
     * @return 分页数据
     */
    @Deprecated
    IPage<EduCourse> getTeacherCoursesPage(Long teacherId, Integer pageNum, Integer pageSize, EduCourse course);
} 