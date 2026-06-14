package com.stat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.stat.model.entity.TeachingEvaluation;
import com.stat.service.TeachingEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 教学评价控制器
 */
@RestController
@RequestMapping("/api/stat/evaluation")
@RequiredArgsConstructor
public class TeachingEvaluationController {

    private final TeachingEvaluationService teachingEvaluationService;

    /**
     * 提交教学评价
     */
    @PostMapping
    @PreAuthorize("hasAuthority('stat:teaching:evaluation')")
    public AjaxResult submitEvaluation(@RequestBody TeachingEvaluation evaluation) {
        boolean success = teachingEvaluationService.submitEvaluation(evaluation);
        return success ? AjaxResult.success(evaluation) : AjaxResult.fail("提交教学评价失败");
    }

    /**
     * 获取教师评价列表
     */
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAuthority('stat:teaching:evaluation')")
    public AjaxResult getTeacherEvaluationList(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        Page<TeachingEvaluation> page = new Page<>(pageNum, pageSize);
        IPage<TeachingEvaluation> result = teachingEvaluationService.getTeacherEvaluationPage(
                page, teacherId, courseId, startTime, endTime);
        return AjaxResult.success(result);
    }

    /**
     * 获取课程评价统计
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('stat:teaching:evaluation')")
    public AjaxResult getCourseEvaluationStats(@PathVariable Long courseId) {
        Map<String, Object> stats = teachingEvaluationService.getCourseEvaluationStats(courseId);
        return AjaxResult.success(stats);
    }

    /**
     * 获取课程评分趋势
     */
    @GetMapping("/course/{courseId}/trend")
    @PreAuthorize("hasAuthority('stat:teaching:evaluation')")
    public AjaxResult getCourseScoreTrend(
            @PathVariable Long courseId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        Map<String, Object> trend = teachingEvaluationService.getCourseScoreTrend(courseId, startTime, endTime);
        return AjaxResult.success(trend);
    }

    /**
     * 获取教师评分统计
     */
    @GetMapping("/course/{courseId}/teacher")
    @PreAuthorize("hasAuthority('stat:teaching:evaluation')")
    public AjaxResult getTeacherScoreStats(@PathVariable Long courseId) {
        Map<String, Object> stats = teachingEvaluationService.getTeacherScoreStats(courseId);
        return AjaxResult.success(stats);
    }

    /**
     * 获取评价列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stat:teaching:evaluation')")
    public AjaxResult list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        Page<TeachingEvaluation> page = new Page<>(pageNum, pageSize);
        IPage<TeachingEvaluation> result = teachingEvaluationService.getEvaluationPage(
                page, teacherName, startTime, endTime);
        return AjaxResult.success(result);
    }
} 