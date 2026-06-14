package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.exam.model.entity.ExamRecord;
import com.exam.model.param.RecodeParam;
import com.exam.model.param.SubmissionParam;
import com.exam.service.ExamRecordService;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.exam.model.vo.ExamRecordVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试记录控制器
 */
@RestController
@RequestMapping("/api/exam/record")
public class
ExamRecordController {
    
    private static final Logger log = LoggerFactory.getLogger(ExamRecordController.class);
    
    @Autowired
    private ExamRecordService recordService;
    
    /**
     * 获取考试记录列表
     */
    @PreAuthorize("hasAnyAuthority('exam:record:list')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamRecord record) {
        IPage<ExamRecord> page = recordService.selectRecordPage(pageNum, pageSize, record);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 获取考试记录详细信息
     */
    @PreAuthorize("hasAnyAuthority('exam:record:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId) {
        return AjaxResult.success(recordService.selectRecordVOById(recordId));
    }
    
    /**
     * 查询考试的统计信息
     */
    @PreAuthorize("hasAnyAuthority('exam:record:stats')")
    @GetMapping(value = "/stats/{examId}")
    public AjaxResult getStats(@PathVariable("examId") Long examId) {
        List<ExamRecord> stats = recordService.selectExamStats(examId);
        return AjaxResult.success(stats);
    }
    
    /**
     * 学生查看自己的考试记录列表
     */
    @PreAuthorize("hasAnyAuthority('exam:record:student')")
    @GetMapping("/student/list")
    public AjaxResult studentList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamRecord record) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getCurrentUserId();
        IPage<ExamRecord> page = recordService.selectStudentRecordPage(pageNum, pageSize, studentId, record);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 学生开始考试
     */
    @PreAuthorize("hasAnyAuthority('exam:record:start')")
    @PostMapping("/start/{examId}")
    public AjaxResult startExam(@PathVariable("examId") Long examId) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getCurrentUserId();
        Long recordId = recordService.startExam(examId, studentId);
        if (recordId != null) {
            return AjaxResult.success(recordId);
        } else {
            return AjaxResult.fail("开始考试失败，可能考试时间已过或已参加过该考试");
        }
    }
    
    /**
     * 学生提交考试
     */
    @PreAuthorize("hasAnyAuthority('exam:record:submit')")
    @PostMapping("/submit")
    public AjaxResult submitExam(@Validated @RequestBody SubmissionParam submissionParam) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getCurrentUserId();
        if (recordService.submitExam(submissionParam, studentId)) {
            return AjaxResult.success("提交考试成功");
        } else {
            return AjaxResult.fail("提交考试失败，考试时间可能已结束或提交异常");
        }
    }
    
    /**
     * 教师批改考试
     */
    @PreAuthorize("hasAnyAuthority('exam:record:grade')")
    @PutMapping("/grade")
    public AjaxResult gradeExam(@Validated @RequestBody RecodeParam recodeParam) {
        if (recodeParam.getRecordId() == null) {
            return AjaxResult.fail("考试记录ID不能为空");
        }
        if (recordService.gradeExam(recodeParam)) {
            return AjaxResult.success("批改考试成功");
        } else {
            return AjaxResult.fail("批改考试失败");
        }
    }
    
    /**
     * 删除考试记录
     */
    @PreAuthorize("hasAnyAuthority('exam:record:remove')")
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(recordService.deleteRecordByIds(recordIds));
    }
    
    /**
     * 获取考试答案
     */
    @GetMapping("/{recordId}/answers")
    @PreAuthorize("hasAuthority('exam:record:query')")
    public AjaxResult getAnswers(@PathVariable Long recordId) {
        try {
            // 获取考试记录
            ExamRecord record = recordService.getById(recordId);
            if (record == null) {
                return AjaxResult.fail("考试记录不存在");
            }
            
            // 直接通过现有接口获取详情数据
            return AjaxResult.success(recordService.selectRecordById(recordId));
        } catch (Exception e) {
            log.error("获取考试记录失败", e);
            return AjaxResult.fail("获取考试记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户ID
     * 注：实际项目中应从Spring Security上下文中获取
     */
    private Long getCurrentUserId() {
        // 这里简化处理，实际项目中应该从安全上下文中获取
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         SysUser user = (SysUser) authentication.getPrincipal();
         if (user == null) {
             return null;
         }
         return user.getUserId();
    }
    
    /**
     * 返回操作结果
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.fail();
    }
} 