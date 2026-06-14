package com.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stat.model.entity.TeachingReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;

/**
 * 教学报告数据访问接口
 */
@Mapper
public interface TeachingReportMapper extends BaseMapper<TeachingReport> {
    
    /**
     * 分页查询教师报告列表
     *
     * @param page 分页参数
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingReport> selectTeacherReportPage(
            Page<TeachingReport> page,
            @Param("teacherId") Long teacherId,
            @Param("courseId") Long courseId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 分页查询课程报告列表
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingReport> selectCourseReportPage(
            Page<TeachingReport> page,
            @Param("courseId") Long courseId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 分页查询报告列表
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @param reportType 报告类型
     * @param reportPeriod 报告周期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<TeachingReport> selectReportPage(
            Page<TeachingReport> page,
            @Param("courseId") Long courseId,
            @Param("teacherId") Long teacherId,
            @Param("reportType") String reportType,
            @Param("reportPeriod") String reportPeriod,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 获取最新的报告
     *
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @param reportType 报告类型
     * @param reportPeriod 报告周期
     * @return 最新报告
     */
    TeachingReport selectLatestReport(
            @Param("courseId") Long courseId,
            @Param("teacherId") Long teacherId,
            @Param("reportType") String reportType,
            @Param("reportPeriod") String reportPeriod
    );
} 