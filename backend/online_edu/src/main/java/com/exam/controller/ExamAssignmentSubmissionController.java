package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.exam.model.entity.ExamAssignmentSubmission;
import com.exam.model.param.SubmissionParam;
import com.exam.model.vo.SubmissionVO;
import com.exam.service.ExamAssignmentSubmissionService;
import com.util.CheckRole;
import com.util.GetId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业提交控制器
 */
@RestController
@RequestMapping("/api/exam/submission")
public class ExamAssignmentSubmissionController {
    
    @Autowired
    private ExamAssignmentSubmissionService submissionService;

    @Autowired
    GetId getId;

    @Autowired
    CheckRole checkRole;
    
    /**
     * 获取作业提交记录列表（教师查看）
     */
    @PreAuthorize("hasAnyAuthority('exam:submission:list')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamAssignmentSubmission submission) {


        IPage<ExamAssignmentSubmission> page = submissionService.selectSubmissionPage(pageNum, pageSize, submission);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 获取作业提交详细信息（教师查看）
     */
    @PreAuthorize("hasAnyAuthority('exam:submission:query')")
    @GetMapping(value = "/{submissionId}")
    public AjaxResult getInfo(@PathVariable("submissionId") Long submissionId) {
        SubmissionVO submission = submissionService.selectSubmissionVOById(submissionId);
        
        // 如果找不到提交记录，返回错误信息
        if (submission == null) {
            return AjaxResult.fail("提交记录不存在");
        }
        
        return AjaxResult.success(submission);
    }
    
    /**
     * 查询作业的提交统计信息
     */
    @PreAuthorize("hasAnyAuthority('exam:submission:stats')")
    @GetMapping(value = "/stats/{assignmentId}")
    public AjaxResult getStats(@PathVariable("assignmentId") Long assignmentId) {
        List<ExamAssignmentSubmission> stats = submissionService.selectSubmissionStatsByAssignmentId(assignmentId);
        return AjaxResult.success(stats);
    }
    
    /**
     * 学生提交作业
     */
    @PreAuthorize("hasAnyAuthority('exam:submission:submit')")
    @PostMapping("/submit")
    public AjaxResult submit(@Validated @RequestBody SubmissionParam submissionParam) {
        // 获取当前登录学生ID (实际项目中需要从登录用户中获取)
        Long studentId = getCurrentUserId();
        return toAjax(submissionService.submitAssignment(submissionParam, studentId));
    }
    
    /**
     * 教师批改作业
     */
    @PreAuthorize("hasAnyAuthority('exam:submission:grade')")
    @PutMapping("/grade")
    public AjaxResult grade(@Validated @RequestBody SubmissionParam submissionParam) {
        if (submissionParam.getSubmissionId() == null) {
            return AjaxResult.fail("提交ID不能为空");
        }
        return toAjax(submissionService.gradeSubmission(submissionParam));
    }
    
    /**
     * 删除作业提交记录
     */
    @PreAuthorize("hasAnyAuthority('exam:submission:remove')")
    @DeleteMapping("/{submissionIds}")
    public AjaxResult remove(@PathVariable Long[] submissionIds) {
        return toAjax(submissionService.deleteSubmissionByIds(submissionIds));
    }
    
    /**
     * 获取当前用户ID
     * 注：实际项目中应从Spring Security上下文中获取
     */
    private Long getCurrentUserId() {
        // 这里简化处理，实际项目中应该从安全上下文中获取

        return getId.getId();
    }
    
    /**
     * 返回操作结果
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.fail();
    }
} 