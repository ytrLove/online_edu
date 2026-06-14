package com.stat.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stat.mapper.TeachingReportMapper;
import com.stat.model.entity.TeachingReport;
import com.stat.service.TeachingReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 教学报告服务实现类
 */
@Service
@RequiredArgsConstructor
public class TeachingReportServiceImpl extends ServiceImpl<TeachingReportMapper, TeachingReport> implements TeachingReportService {

    private final TeachingReportMapper teachingReportMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateReport(Long courseId, LocalDateTime startTime, LocalDateTime endTime) {
        TeachingReport report = new TeachingReport();
        report.setCourseId(courseId);
        report.setStartTime(startTime);
        report.setEndTime(endTime);
        report.setGenerateTime(LocalDateTime.now());
        
        // TODO: 根据业务需求生成报告内容
        // 1. 统计课程学习情况
        // 2. 统计教学评价情况
        // 3. 生成教学建议
        
        return save(report);
    }

    @Override
    public IPage<TeachingReport> getTeacherReportPage(
            Page<TeachingReport> page,
            Long teacherId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return teachingReportMapper.selectTeacherReportPage(page, teacherId, courseId, startTime, endTime);
    }

    @Override
    public IPage<TeachingReport> getCourseReportPage(
            Page<TeachingReport> page,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return teachingReportMapper.selectCourseReportPage(page, courseId, startTime, endTime);
    }

    @Override
    public TeachingReport getReportDetail(Long reportId) {
        return getById(reportId);
    }
} 