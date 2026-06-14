package com.stat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.stat.model.entity.TeachingReport;
import com.stat.service.TeachingReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 教学报告控制器
 */
@RestController
@RequestMapping("/api/stat/report")
@RequiredArgsConstructor
public class TeachingReportController {

    private final TeachingReportService teachingReportService;

    /**
     * 生成教学报告
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('stat:teaching:report')")
    public AjaxResult generateReport(
            @RequestParam Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam String reportType,
            @RequestParam String reportPeriod
    ) {
        // 根据报告周期设置时间范围
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = calculateStartTime(reportPeriod, endTime);
        
        boolean success = teachingReportService.generateReport(courseId, startTime, endTime);
        return success ? AjaxResult.success(true) : AjaxResult.fail("生成教学报告失败");
    }

    /**
     * 获取教学报告
     */
    @GetMapping("/{reportId}")
    @PreAuthorize("hasAuthority('stat:teaching:query')")
    public AjaxResult getReport(@PathVariable Long reportId) {
        TeachingReport report = teachingReportService.getReportDetail(reportId);
        return AjaxResult.success(report);
    }

    /**
     * 获取课程报告分页列表
     */
    @GetMapping("/course")
    @PreAuthorize("hasAuthority('stat:teaching:query')")
    public AjaxResult getCourseReportPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        Page<TeachingReport> page = new Page<>(pageNum, pageSize);
        IPage<TeachingReport> result = teachingReportService.getCourseReportPage(
                page, courseId, startTime, endTime);
        return AjaxResult.success(result);
    }

    /**
     * 获取报告列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stat:teaching:query')")
    public AjaxResult getReportList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String reportPeriod,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        Page<TeachingReport> page = new Page<>(pageNum, pageSize);
        IPage<TeachingReport> result = teachingReportService.getTeacherReportPage(
                page, teacherId, courseId, startTime, endTime);
        return AjaxResult.success(result);
    }

    /**
     * 导出教学报告
     */
    @GetMapping("/export/{reportId}")
    @PreAuthorize("hasAuthority('stat:teaching:export')")
    public AjaxResult exportReport(
            @PathVariable Long reportId,
            @RequestParam(defaultValue = "pdf") String format
    ) {
        // TODO: 实现导出功能
        return AjaxResult.fail("导出功能尚未实现");
    }

    /**
     * 根据报告周期计算开始时间
     */
    private LocalDateTime calculateStartTime(String reportPeriod, LocalDateTime endTime) {
        return switch (reportPeriod) {
            case "weekly" -> endTime.minusWeeks(1);
            case "monthly" -> endTime.minusMonths(1);
            case "term" -> endTime.minusMonths(4); // 假设一个学期为4个月
            default -> endTime.minusMonths(1);
        };
    }
} 