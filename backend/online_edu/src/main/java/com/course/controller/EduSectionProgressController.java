package com.course.controller;

import com.common.core.domain.AjaxResult;
import com.course.model.entity.EduSectionProgress;
import com.course.service.EduSectionProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程学习进度控制器
 */
@RestController
@RequestMapping("/course/progress")
public class EduSectionProgressController {

    @Autowired
    private EduSectionProgressService progressService;

    /**
     * 获取学生在某个小节的学习进度
     */
    @GetMapping("/student/{studentId}/section/{sectionId}")
    public AjaxResult getProgressBySection(
            @PathVariable Long studentId,
            @PathVariable Long sectionId) {
        EduSectionProgress progress = progressService.getProgressByStudentAndSection(studentId, sectionId);
        return AjaxResult.success("获取学习进度成功", progress);
    }

    /**
     * 获取学生在某个课程的所有学习进度
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    public AjaxResult getProgressByCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<EduSectionProgress> progressList = progressService.getProgressByCourseAndStudent(courseId, studentId);
        return AjaxResult.success("获取学习进度成功", progressList);
    }

    /**
     * 计算学生在课程中的总体学习进度
     */
    @GetMapping("/total/student/{studentId}/course/{courseId}")
    public AjaxResult calculateCourseProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        Integer progress = progressService.calculateCourseProgress(courseId, studentId);
        return AjaxResult.success("获取课程总体进度成功", progress);
    }

    /**
     * 更新学习进度
     */
    @PostMapping("/update")
    public AjaxResult updateProgress(
            @RequestParam Long studentId,
            @RequestParam Long sectionId,
            @RequestParam Integer progress,
            @RequestParam Integer position) {
        return progressService.updateProgress(studentId, sectionId, progress, position);
    }

    /**
     * 标记小节为已完成
     */
    @PostMapping("/complete")
    public AjaxResult completeSection(
            @RequestParam Long studentId,
            @RequestParam Long sectionId) {
        return progressService.completeSection(studentId, sectionId);
    }
} 