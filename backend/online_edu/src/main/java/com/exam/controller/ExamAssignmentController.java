package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.exam.model.entity.ExamAssignment;
import com.exam.model.param.AssignmentParam;
import com.exam.model.param.AssignmentQuestionParam;
import com.exam.service.ExamAssignmentService;
import com.user.model.entity.SysUser;
import com.course.service.EduTeacherService;
import com.course.model.entity.EduTeacher;
import com.util.GetId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 作业控制器
 */
@RestController
@RequestMapping("/api/exam/assignment")
public class ExamAssignmentController {
    
    @Autowired
    private ExamAssignmentService assignmentService;
    
    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private GetId getId;
    
    /**
     * 获取作业列表
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:query')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamAssignment assignment) {

        //  获取当前用户ID
        IPage<ExamAssignment> page = assignmentService.selectAssignmentPage(pageNum, pageSize, assignment);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 获取作业详细信息
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:query')")
    @GetMapping(value = "/{assignmentId}")
    public AjaxResult getInfo(@PathVariable("assignmentId") Long assignmentId) {
        return AjaxResult.success(assignmentService.selectAssignmentVOById(assignmentId));
    }
    
    /**
     * 学生查看自己的作业列表
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:query')")
    @GetMapping("/student/list")
    public AjaxResult studentList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamAssignment assignment) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getId.getId();
        IPage<ExamAssignment> page = assignmentService.selectStudentAssignmentPage(pageNum, pageSize, studentId, assignment);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 学生查看自己的作业详情
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:query')")
    @GetMapping("/student/{assignmentId}")
    public AjaxResult studentAssignment(@PathVariable("assignmentId") Long assignmentId) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getId.getId();
        return AjaxResult.success(assignmentService.selectStudentAssignmentVOById(assignmentId, studentId));
    }
    
    /**
     * 新增作业
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AssignmentParam assignmentParam) {
        // 获取当前用户ID
        Long userId = getCurrentUserId();
        
        // 通过用户ID查询教师信息
        EduTeacher teacher = teacherService.getTeacherByUserId(userId);
        if (teacher == null) {
            return AjaxResult.fail("当前用户不是教师，无法创建作业");
        }
        
        // 设置教师ID
        assignmentParam.setTeacherId(teacher.getTeacherId());
        return toAjax(assignmentService.insertAssignment(assignmentParam));
    }
    
    /**
     * 修改作业
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AssignmentParam assignmentParam) {
        if (assignmentParam.getAssignmentId() == null) {
            return AjaxResult.fail("作业ID不能为空");
        }
        return toAjax(assignmentService.updateAssignment(assignmentParam));
    }
    
    /**
     * 删除作业
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:remove')")
    @DeleteMapping("/{assignmentIds}")
    public AjaxResult remove(@PathVariable Long[] assignmentIds) {
        return toAjax(assignmentService.deleteAssignmentByIds(assignmentIds));
    }
    
    /**
     * 关联题目到作业
     */
    @PreAuthorize("hasAnyAuthority('exam:assignment:edit')")
    @PostMapping("/associate-questions")
    public AjaxResult associateQuestions(@Validated @RequestBody AssignmentQuestionParam param) {
        if (param.getAssignmentId() == null) {
            return AjaxResult.fail("作业ID不能为空");
        }
        return toAjax(assignmentService.associateQuestions(param));
    }
    
    /**
     * 获取当前用户ID
     * 注：实际项目中应从Spring Security上下文中获取
     */
    private Long getCurrentUserId() {
        // 这里简化处理，实际项目中应该从安全上下文中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();

        return user.getUserId();
    }
    
    /**
     * 返回操作结果
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.fail();
    }
} 