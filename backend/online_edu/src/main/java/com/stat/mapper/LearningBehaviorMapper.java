package com.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.stat.model.entity.LearningBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;

/**
 * 学习行为数据访问接口
 */
@Mapper
public interface LearningBehaviorMapper extends BaseMapper<LearningBehavior> {
    
    /**
     * 分页查询学习行为记录
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param behaviorType 行为类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<LearningBehavior> selectBehaviorPage(
            Page<LearningBehavior> page,
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("behaviorType") String behaviorType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 统计指定时间段内的行为次数
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param behaviorType 行为类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 行为次数
     */
    Integer countBehavior(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("behaviorType") String behaviorType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 统计指定时间段内的总学习时长（分钟）
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总学习时长（分钟）
     */
    Integer sumStudyDuration(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
} 