package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.course.mapper.EduStudentMapper;
import com.course.mapper.EduTeacherMapper;
import com.course.model.dto.request.CourseCreateRequest;
import com.course.model.dto.request.CourseQueryRequest;
import com.course.model.dto.request.CourseUpdateRequest;
import com.course.model.dto.response.CourseDetailResponse;
import com.course.model.dto.response.CourseListResponse;
import com.course.model.entity.EduStudent;
import com.course.model.entity.EduTeacher;
import com.course.service.EduCourseService;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/api/edu/course")
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduStudentMapper studentMapper;
    
    @Autowired
    private EduTeacherMapper teacherMapper;

    /**
     * 分页查询课程列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('edu:course:query')")
    public AjaxResult<IPage<CourseListResponse>> list(CourseQueryRequest request) {
        IPage<CourseListResponse> page = courseService.listCourses(request);
        return AjaxResult.success(page);
    }

    /**
     * 获取当前登录教师的课程列表
     */
    @GetMapping("/current/list")
    @PreAuthorize("hasAuthority('edu:course:query')")
    public AjaxResult<?> getCurrentTeacherCourses(CourseQueryRequest request) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return AjaxResult.fail("未登录");
        }
        
        SysUser currentUser = (SysUser) authentication.getPrincipal();
        
        // 通过用户ID获取教师信息
        QueryWrapper<EduTeacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getUserId());
        EduTeacher teacher = teacherMapper.selectOne(queryWrapper);
        
        if (teacher == null) {
            return AjaxResult.fail("当前用户不是教师");
        }
        
        // 设置教师ID
        request.setTeacherId(teacher.getTeacherId());
        
        // 获取教师的课程列表（分页）
        IPage<CourseListResponse> page = courseService.listCourses(request);
        return AjaxResult.success(page);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("hasAuthority('edu:course:query')")
    public AjaxResult<CourseDetailResponse> getInfo(@PathVariable Long courseId) {
        CourseDetailResponse course = courseService.getCourseDetailById(courseId);
        return AjaxResult.success(course);
    }

    /**
     * 新增课程
     */
    @PostMapping
    @PreAuthorize("hasAuthority('edu:course:add')")
    public AjaxResult<CourseDetailResponse> add(@RequestBody CourseCreateRequest request) {
        CourseDetailResponse result = courseService.createCourse(request);
        return AjaxResult.success(result);
    }

    /**
     * 修改课程
     */
    @PutMapping
    @PreAuthorize("hasAuthority('edu:course:edit')")
    public AjaxResult<CourseDetailResponse> update(@RequestBody CourseUpdateRequest request) {
        CourseDetailResponse result = courseService.updateCourse(request);
        return AjaxResult.success(result);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAuthority('edu:course:remove')")
    public AjaxResult<Boolean> remove(@PathVariable Long courseId) {
        boolean result = courseService.deleteCourse(courseId);
        return AjaxResult.success(result);
    }

    /**
     * 更新课程状态
     */
    @PutMapping("/status/{courseId}/{status}")
    @PreAuthorize("hasAuthority('edu:course:edit')")
    public AjaxResult<Boolean> updateStatus(@PathVariable Long courseId, @PathVariable String status) {
        boolean result = courseService.updateCourseStatus(courseId, status);
        return AjaxResult.success(result);
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAuthority('edu:course:query')")
    public AjaxResult<List<CourseListResponse>> getTeacherCourses(@PathVariable Long teacherId) {
        CourseQueryRequest request = new CourseQueryRequest();
        request.setTeacherId(teacherId);
        request.setPageSize(1000); // 设置足够大的页面大小以获取所有课程
        List<CourseListResponse> courses = courseService.listCourses(request).getRecords();
        return AjaxResult.success(courses);
    }
    
    /**
     * 获取课程学生列表
     */
    @GetMapping("/{courseId}/students")
    @PreAuthorize("hasAuthority('edu:course:query')")
    public AjaxResult<List<Map<String, Object>>> getCourseStudents(@PathVariable Long courseId) {
        List<Map<String, Object>> students = courseService.getCourseStudents(courseId);
        return AjaxResult.success(students);
    }
    
    /**
    * 加入课程
     */
    @GetMapping("/enroll/{courseId}")
    public AjaxResult enroll(@PathVariable("courseId") Long courseId) {
        // 获取学生id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return AjaxResult.fail("未登录");
        }
        SysUser currentUser = (SysUser) authentication.getPrincipal();
        // 通过用户id获取学生信息
        QueryWrapper<EduStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getUserId());
        EduStudent student = studentMapper.selectOne(queryWrapper);
        boolean result = courseService.enroll(courseId, student.getStudentId());
        if (!result){
            return AjaxResult.fail("已加入该课程");
        }
        return AjaxResult.success("添加成功");
    }

} 