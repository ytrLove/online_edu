package com.stat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stat.model.entity.TeachingEvaluation;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 教学评价服务接口
 */
public interface TeachingEvaluationService {
    
    /**
     * 提交教学评价
     *
     * @param evaluation 教学评价
     * @return 是否成功
     */
    boolean submitEvaluation(TeachingEvaluation evaluation);
    
    /**
     * 获取课程的教师ID
     *
     * @param courseId 课程ID
     * @return 教师ID
     */
    Long getTeacherIdByCourseId(Long courseId);
    
    /**
     * 分页查询教师评价列表
     *
     * @param page 分页参数
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingEvaluation> getTeacherEvaluationPage(
            Page<TeachingEvaluation> page,
            Long teacherId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
    
    /**
     * 获取课程评价统计
     *
     * @param courseId 课程ID
     * @return 评价统计信息
     */
    Map<String, Object> getCourseEvaluationStats(Long courseId);
    
    /**
     * 获取课程评分趋势
     *
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评分趋势数据
     */
    Map<String, Object> getCourseScoreTrend(
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
    
    /**
     * 获取教师评分统计
     *
     * @param courseId 课程ID
     * @return 教师评分统计
     */
    Map<String, Object> getTeacherScoreStats(Long courseId);

    /**
     * 获取评价列表
     *
     * @param page 分页参数
     * @param teacherName 教师姓名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingEvaluation> getEvaluationPage(
            Page<TeachingEvaluation> page,
            String teacherName,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}