package com.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.stat.model.entity.LearningSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 学习情况汇总Mapper接口
 */
@Mapper
public interface LearningSummaryMapper extends BaseMapper<LearningSummary> {
    
    /**
     * 获取学习情况汇总分页列表
     *
     * @param page 分页参数
     * @param studentName 学生姓名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<LearningSummary> selectSummaryPage(
            Page<LearningSummary> page,
            @Param("studentName") String studentName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取学生的学习情况详情
     *
     * @param studentId 学生ID
     * @return 学习情况详情
     */
    Map<String, Object> selectStudentSummary(@Param("studentId") Long studentId);
    
    /**
     * 获取课程的学习情况统计
     *
     * @param courseId 课程ID
     * @return 学习情况统计
     */
    Map<String, Object> selectCourseSummary(@Param("courseId") Long courseId);
    
    /**
     * 获取学生的课程学习概要
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学习概要
     */
    LearningSummary selectByStudentAndCourse(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId
    );
    
    /**
     * 获取学生的所有课程学习概要
     *
     * @param studentId 学生ID
     * @return 学习概要列表
     */
    List<LearningSummary> selectByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 分页查询课程的学习概要
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 分页结果
     */
    IPage<LearningSummary> selectByCourseId(
            Page<LearningSummary> page,
            @Param("courseId") Long courseId
    );
    
    /**
     * 更新学习概要数据
     *
     * @param summary 学习概要
     * @return 影响行数
     */
    int updateSummary(LearningSummary summary);

    /**
     * 生成学习情况汇总
     *
     * @param studentId 学生ID（可选）
     * @param courseId 课程ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 影响行数
     */
    int generateSummary(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
} 