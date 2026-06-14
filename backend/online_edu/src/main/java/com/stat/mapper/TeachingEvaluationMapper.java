package com.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stat.model.entity.TeachingEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 教学评价Mapper接口
 */
@Mapper
public interface TeachingEvaluationMapper extends BaseMapper<TeachingEvaluation> {
    
    /**
     * 获取评价列表
     *
     * @param page 分页参数
     * @param teacherName 教师姓名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingEvaluation> selectEvaluationPage(
            Page<TeachingEvaluation> page,
            @Param("teacherName") String teacherName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
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
    IPage<TeachingEvaluation> selectTeacherEvaluationPage(
            Page<TeachingEvaluation> page,
            @Param("teacherId") Long teacherId,
            @Param("courseId") Long courseId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取课程评价统计
     *
     * @param courseId 课程ID
     * @return 评价统计信息
     */
    Map<String, Object> selectCourseEvaluationStats(@Param("courseId") Long courseId);
    
    /**
     * 获取课程评分趋势
     *
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评分趋势数据
     */
    Map<String, Object> selectCourseScoreTrend(
            @Param("courseId") Long courseId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取教师评分统计
     *
     * @param courseId 课程ID
     * @return 教师评分统计
     */
    Map<String, Object> selectTeacherScoreStats(@Param("courseId") Long courseId);
}