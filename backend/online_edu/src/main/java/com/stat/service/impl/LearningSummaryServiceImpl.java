package com.stat.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stat.mapper.LearningSummaryMapper;
import com.stat.model.entity.LearningSummary;
import com.stat.service.LearningSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 学习情况汇总服务实现类
 */
@Service
@RequiredArgsConstructor
public class LearningSummaryServiceImpl extends ServiceImpl<LearningSummaryMapper, LearningSummary> implements LearningSummaryService {

    private final LearningSummaryMapper learningSummaryMapper;

    @Override
    public IPage<LearningSummary> getSummaryPage(
            Page<LearningSummary> page,
            String studentName,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return learningSummaryMapper.selectSummaryPage(page, studentName, startTime, endTime);
    }

    @Override
    public Map<String, Object> getStudentSummary(Long studentId) {
        return learningSummaryMapper.selectStudentSummary(studentId);
    }

    @Override
    public Map<String, Object> getCourseSummary(Long courseId) {
        return learningSummaryMapper.selectCourseSummary(courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateSummary(
            Long studentId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        // 如果没有指定时间范围，默认统计所有时间
        if (startTime == null) {
            startTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        // 生成学习情况汇总
        return learningSummaryMapper.generateSummary(studentId, courseId, startTime, endTime) > 0;
    }
} 