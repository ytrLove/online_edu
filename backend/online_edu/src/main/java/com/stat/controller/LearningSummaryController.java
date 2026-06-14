package com.stat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.stat.model.entity.LearningSummary;
import com.stat.service.LearningSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 学习情况汇总控制器
 */
@RestController
@RequestMapping("/api/stat/summary")
@RequiredArgsConstructor
public class LearningSummaryController {

    private final LearningSummaryService learningSummaryService;

    /**
     * 获取学习情况汇总列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stat:learning:summary')")
    public AjaxResult list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        Page<LearningSummary> page = new Page<>(pageNum, pageSize);
        IPage<LearningSummary> result = learningSummaryService.getSummaryPage(
                page, studentName, startTime, endTime);
        return AjaxResult.success(result);
    }

    /**
     * 获取学生的学习情况详情
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('stat:learning:summary')")
    public AjaxResult getStudentSummary(@PathVariable Long studentId) {
        return AjaxResult.success(learningSummaryService.getStudentSummary(studentId));
    }

    /**
     * 获取课程的学习情况统计
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('stat:learning:summary')")
    public AjaxResult getCourseSummary(@PathVariable Long courseId) {
        return AjaxResult.success(learningSummaryService.getCourseSummary(courseId));
    }

    /**
     * 生成学习情况汇总
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('stat:learning:generate')")
    public AjaxResult generateSummary(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        boolean success = learningSummaryService.generateSummary(studentId, courseId, startTime, endTime);
        return success ? AjaxResult.success("生成学习情况汇总成功") : AjaxResult.fail("生成学习情况汇总失败");
    }
} 