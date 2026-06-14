package com.stat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stat.model.entity.LearningSummary;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 学习情况汇总服务接口
 */
public interface LearningSummaryService {
    
    /**
     * 获取学习情况汇总分页列表
     *
     * @param page 分页参数
     * @param studentName 学生姓名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<LearningSummary> getSummaryPage(
            Page<LearningSummary> page,
            String studentName,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
    
    /**
     * 获取学生的学习情况详情
     *
     * @param studentId 学生ID
     * @return 学习情况详情
     */
    Map<String, Object> getStudentSummary(Long studentId);
    
    /**
     * 获取课程的学习情况统计
     *
     * @param courseId 课程ID
     * @return 学习情况统计
     */
    Map<String, Object> getCourseSummary(Long courseId);

    /**
     * 生成学习情况汇总
     *
     * @param studentId 学生ID（可选）
     * @param courseId 课程ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 是否成功
     */
    boolean generateSummary(
            Long studentId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
} 