package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduTeacher;
import com.course.service.EduTeacherService;
import com.util.GetId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师管理控制器
 */
@RestController
@RequestMapping("/api/edu/teacher")
public class EduTeacherController {

    @Autowired
    private GetId getId;

    @Autowired
    private EduTeacherService teacherService;

    /**
     * 分页查询教师列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('edu:teacher:query')")
    public AjaxResult<IPage<EduTeacher>> list(EduTeacher teacher,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<EduTeacher> page = teacherService.listTeachers(teacher, pageNum, pageSize);
        return AjaxResult.success(page);
    }

    /**
     * 获取教师详情
     */
    @GetMapping("/{teacherId}")
    @PreAuthorize("hasAuthority('edu:teacher:query')")
    public AjaxResult<EduTeacher> getInfo(@PathVariable Long teacherId) {
        EduTeacher teacher = teacherService.getTeacherById(teacherId);
        return AjaxResult.success(teacher);
    }

    /**
     * 新增教师
     */
    @PostMapping
    @PreAuthorize("hasAuthority('edu:teacher:add')")
    public AjaxResult<EduTeacher> add(@RequestBody EduTeacher teacher) {
        EduTeacher result = teacherService.createTeacher(teacher);
        return AjaxResult.success(result);
    }

    /**
     * 修改教师
     */
    @PutMapping
    @PreAuthorize("hasAuthority('edu:teacher:edit')")
    public AjaxResult<EduTeacher> update(@RequestBody EduTeacher teacher) {
        EduTeacher result = teacherService.updateTeacher(teacher);
        return AjaxResult.success(result);
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{teacherId}")
    @PreAuthorize("hasAuthority('edu:teacher:remove')")
    public AjaxResult<Boolean> remove(@PathVariable Long teacherId) {
        boolean result = teacherService.deleteTeacher(teacherId);
        return AjaxResult.success(result);
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/course/list")
    @PreAuthorize("hasAuthority('edu:teacher:query')")
    public AjaxResult<List<EduCourse>> getCourses(@RequestParam("pageNum") Integer pageNum,
                                                  @RequestParam("pageSize") Integer pageSize,
                                                  EduCourse course
                                                  ) {
        List<EduCourse> courses = teacherService.getTeacherCourses(pageNum, pageSize, course);
        return AjaxResult.success(courses);
    }


} 