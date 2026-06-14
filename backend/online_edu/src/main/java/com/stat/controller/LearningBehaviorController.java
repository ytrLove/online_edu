package com.stat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.stat.model.entity.LearningBehavior;
import com.stat.model.vo.BehaviorAnalysisVO;
import com.stat.model.vo.BehaviorStatsVO;
import com.stat.service.LearningBehaviorService;
import com.util.GetId;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 学习行为控制器
 */
@RestController
@RequestMapping("/api/stat/behavior")
@RequiredArgsConstructor
public class LearningBehaviorController {

    private final LearningBehaviorService learningBehaviorService;
    private final GetId getId;

    /**
     * 记录学习行为
     */
    @PostMapping
    @PreAuthorize("permitAll()")
    public AjaxResult recordBehavior(@RequestBody LearningBehavior behavior) {
        boolean success = learningBehaviorService.recordBehavior(behavior);
        return success ? AjaxResult.success(behavior) : AjaxResult.fail("记录学习行为失败");
    }

    /**
     * 批量记录学习行为
     */
    @PostMapping("/batch")
    @PreAuthorize("permitAll()")
    public AjaxResult recordBehaviors(@RequestBody List<LearningBehavior> behaviors) {
        boolean success = learningBehaviorService.recordBehaviors(behaviors);
        return success ? AjaxResult.success(true) : AjaxResult.fail("批量记录学习行为失败");
    }

    /**
     * 查询学习行为数据
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stat:behavior:query')")
    public AjaxResult getBehaviorList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime
    ) {
        Page<LearningBehavior> page = new Page<>(pageNum, pageSize);
        if (studentId == null){
            studentId = getId.getId();
        }
        
        LocalDateTime startDateTime = startTime != null ? startTime.atStartOfDay() : null;
        LocalDateTime endDateTime = endTime != null ? endTime.plusDays(1).atStartOfDay() : null;
        
        IPage<LearningBehavior> result = learningBehaviorService.getBehaviorPage(
                page, studentId, courseId, behaviorType, startDateTime, endDateTime);
        return AjaxResult.success(result);
    }
    
    /**
     * 获取学习行为统计数据
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('stat:behavior:query')")
    public AjaxResult getBehaviorStats(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime
    ) {
        LocalDateTime startDateTime = startTime != null ? startTime.atStartOfDay() : null;
        LocalDateTime endDateTime = endTime != null ? endTime.plusDays(1).atStartOfDay() : null;
        
        BehaviorStatsVO stats = learningBehaviorService.getBehaviorStats(courseId, startDateTime, endDateTime);
        return AjaxResult.success(stats);
    }
    
    /**
     * 获取特定学生的行为分析
     */
    @GetMapping("/student/{studentId}/analysis")
    @PreAuthorize("hasAuthority('stat:behavior:query')")
    public AjaxResult getStudentBehaviorAnalysis(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime
    ) {
        LocalDateTime startDateTime = startTime != null ? startTime.atStartOfDay() : null;
        LocalDateTime endDateTime = endTime != null ? endTime.plusDays(1).atStartOfDay() : null;
        
        BehaviorAnalysisVO analysis = learningBehaviorService.getStudentBehaviorAnalysis(
                studentId, courseId, startDateTime, endDateTime);
        return AjaxResult.success(analysis);
    }
    
    /**
     * 获取特定课程的行为分析
     */
    @GetMapping("/course/{courseId}/analysis")
    @PreAuthorize("hasAuthority('stat:behavior:query')")
    public AjaxResult getCourseBehaviorAnalysis(
            @PathVariable Long courseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime
    ) {
        LocalDateTime startDateTime = startTime != null ? startTime.atStartOfDay() : null;
        LocalDateTime endDateTime = endTime != null ? endTime.plusDays(1).atStartOfDay() : null;
        
        BehaviorAnalysisVO analysis = learningBehaviorService.getCourseBehaviorAnalysis(
                courseId, startDateTime, endDateTime);
        return AjaxResult.success(analysis);
    }
    
    /**
     * 导出行为数据
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('stat:behavior:export')")
    public ResponseEntity<byte[]> exportBehaviorData(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime
    ) {
        LocalDateTime startDateTime = startTime != null ? startTime.atStartOfDay() : null;
        LocalDateTime endDateTime = endTime != null ? endTime.plusDays(1).atStartOfDay() : null;
        
        byte[] data = learningBehaviorService.exportBehaviorData(studentId, courseId, behaviorType, startDateTime, endDateTime);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String fileName = "learning_behavior_" + LocalDate.now() + ".xlsx";
        headers.setContentDispositionFormData("attachment", fileName);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}