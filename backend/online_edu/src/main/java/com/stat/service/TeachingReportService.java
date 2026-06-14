package com.stat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stat.model.entity.TeachingReport;

import java.time.LocalDateTime;

/**
 * 教学报告服务接口
 */
public interface TeachingReportService extends IService<TeachingReport> {

    /**
     * 生成教学报告
     *
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否成功
     */
    boolean generateReport(Long courseId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取教师报告分页列表
     *
     * @param page 分页参数
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingReport> getTeacherReportPage(
            Page<TeachingReport> page,
            Long teacherId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 获取课程报告分页列表
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingReport> getCourseReportPage(
            Page<TeachingReport> page,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 获取报告详情
     *
     * @param reportId 报告ID
     * @return 报告详情
     */
    TeachingReport getReportDetail(Long reportId);
}