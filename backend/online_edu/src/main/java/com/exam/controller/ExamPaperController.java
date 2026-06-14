package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.exam.model.entity.ExamPaper;
import com.exam.model.entity.ExamPaperQuestion;
import com.exam.model.param.ExamPaperParam;
import com.exam.service.ExamPaperService;
import com.user.model.entity.SysUser;
import com.course.service.EduTeacherService;
import com.course.model.entity.EduTeacher;
import com.util.GetId;
import com.util.CheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试试卷控制器
 */
@RestController
@RequestMapping("/api/exam/paper")
public class ExamPaperController {
    
    @Autowired
    private ExamPaperService examPaperService;

    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private CheckRole checkRole;

    @Autowired
    private GetId getId;

    /**
     * 获取试卷列表
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:query')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamPaper exam) {
        IPage<ExamPaper> page = examPaperService.selectExamPage(pageNum, pageSize, exam);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 获取试卷详细信息
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:query')")
    @GetMapping(value = "/{examId}")
    public AjaxResult getInfo(@PathVariable("examId") Long examId) {
        return AjaxResult.success(examPaperService.selectExamVOById(examId));
    }
    
    /**
     * 学生查看自己的试卷列表
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:student')")
    @GetMapping("/student/list")
    public AjaxResult studentList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamPaper exam) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getId.getId();
        IPage<ExamPaper> page = examPaperService.selectStudentExamPage(pageNum, pageSize, studentId, exam);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }

    /**
     * 学生查看自己的试卷详情
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:student')")
    @GetMapping("/student/{examId}")
    public AjaxResult studentExam(@PathVariable("examId") Long examId) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getId.getId();
        return AjaxResult.success(examPaperService.selectStudentExamVOById(examId, studentId));
    }
    
    /**
     * 新增试卷
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ExamPaperParam examParam) {
        // 获取当前用户ID
        Long userId = getCurrentUserId();
        
        // 如果是教师，获取教师ID
        if (checkRole.isTeacher()) {
            EduTeacher teacher = teacherService.getTeacherByUserId(userId);
            if (teacher != null) {
                examParam.setTeacherId(teacher.getTeacherId());
            } else {
                return AjaxResult.fail("未找到对应的教师信息");
            }
        } else if (checkRole.isAdmin()) {
            // 如果是管理员，不设置教师ID
            examParam.setTeacherId(null);
        } else {
            return AjaxResult.fail("只有教师和管理员可以创建试卷");
        }
        
        return toAjax(examPaperService.insertExam(examParam));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();
        if (user != null) {
            return user.getUserId();
        }
        return null;
    }

    /**
     * 修改试卷
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ExamPaperParam examParam) {
        if (examParam.getExamId() == null) {
            return AjaxResult.fail("试卷ID不能为空");
        }
        // 设置当前更新者ID (实际项目中需要从登录用户中获取)
        examParam.setUpdateBy(getCurrentUserId().toString());
        boolean result = examPaperService.updateExam(examParam);
        if (!result) {
            return AjaxResult.fail("试卷已有学生参加考试，无法修改");
        }
        return AjaxResult.success();
    }
    
    /**
     * 删除试卷
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:remove')")
    @DeleteMapping("/{examIds}")
    public AjaxResult remove(@PathVariable Long[] examIds) {
        boolean result = examPaperService.deleteExamByIds(examIds);
        if (!result) {
            return AjaxResult.fail("试卷已有学生参加考试，无法删除");
        }
        return AjaxResult.success();
    }

    /**
     * 获取试卷题目列表
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:query')")
    @GetMapping("/{examId}/questions")
    public AjaxResult questionList(@PathVariable("examId") Long examId) {
        return AjaxResult.success(examPaperService.selectExamQuestionList(examId));
    }

    /**
     * 批量处理试卷题目
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:edit')")
    @PostMapping("/question/batch")
    public AjaxResult questionBatch(@RequestBody List<ExamPaperQuestion> examParam) {
        // 批量插入
        boolean result =  examPaperService.insertExamQuestions(examParam);
        // 更新试卷题目
        return AjaxResult.success(result);
    }

    /**
     * 从试卷中删除题目
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:edit')")
    @DeleteMapping("/{examId}/question/{questionId}")
    public AjaxResult removeQuestion(@PathVariable("examId") Long examId, @PathVariable("questionId") Long questionId) {
        boolean result = examPaperService.deleteExamQuestion(examId, questionId);
        if (!result) {
            return AjaxResult.fail("删除失败，可能试卷已有学生参加考试");
        }
        return AjaxResult.success(result);
    }
    /**
     * 下架试卷
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:edit')")
    @PutMapping("/unpublish/{examId}")
    public AjaxResult unpublish(@PathVariable("examId") Long examId) {
        boolean result = examPaperService.unpublishExam(examId);
        if (!result) {
            return AjaxResult.fail("下架失败，可能试卷已有学生参加考试");
        }
        return AjaxResult.success(result);
    }
    /*
    *  发布
     */
    @PreAuthorize("hasAnyAuthority('exam:paper:edit')")
    @PutMapping("/publish/{examId}")
    public AjaxResult publish(@PathVariable("examId") Long examId) {
        boolean result = examPaperService.publishExam(examId);
        if (!result) {
            return AjaxResult.fail("发布失败，可能试卷已有学生参加考试");
        }
        return AjaxResult.success(result);
    }

    /**
     * 返回操作结果
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.fail();
    }
} 