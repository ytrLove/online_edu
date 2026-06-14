package com.stat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stat.model.entity.LearningBehavior;
import com.stat.model.vo.BehaviorAnalysisVO;
import com.stat.model.vo.BehaviorStatsVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习行为记录服务接口
 */
public interface LearningBehaviorService {
    
    /**
     * 记录学习行为
     *
     * @param behavior 学习行为
     * @return 是否成功
     */
    boolean recordBehavior(LearningBehavior behavior);
    
    /**
     * 批量记录学习行为
     *
     * @param behaviors 学习行为列表
     * @return 是否成功
     */
    boolean recordBehaviors(List<LearningBehavior> behaviors);
    
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
    IPage<LearningBehavior> getBehaviorPage(
            Page<LearningBehavior> page,
            Long studentId,
            Long courseId,
            String behaviorType,
            LocalDateTime startTime,
            LocalDateTime endTime
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
            Long studentId,
            Long courseId,
            String behaviorType,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
    
    /**
     * 统计指定时间段内的总学习时长（分钟）
     *
     * @param studentId 学生ID
     * @param courseId  课程ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param now
     * @return 总学习时长（分钟）
     */
    Integer sumStudyDuration(
            Long studentId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime now);
            
    /**
     * 获取学习行为统计数据
     *
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据
     */
    BehaviorStatsVO getBehaviorStats(
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
    
    /**
     * 获取学生行为分析
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 学生行为分析
     */
    BehaviorAnalysisVO getStudentBehaviorAnalysis(
            Long studentId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
    
    /**
     * 获取课程行为分析
     *
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 课程行为分析
     */
    BehaviorAnalysisVO getCourseBehaviorAnalysis(
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
    
    /**
     * 导出行为数据
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param behaviorType 行为类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 导出的二进制数据
     */
    byte[] exportBehaviorData(
            Long studentId,
            Long courseId,
            String behaviorType,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
} 