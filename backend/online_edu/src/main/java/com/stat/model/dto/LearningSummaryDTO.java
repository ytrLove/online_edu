package com.stat.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习概要数据传输对象
 */
@Data
public class LearningSummaryDTO {
    
    /**
     * 概要ID
     */
    private Long summaryId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 总学习时长(分钟)
     */
    private Integer totalStudyTime;
    
    /**
     * 资源访问次数
     */
    private Integer resourceAccessCount;
    
    /**
     * 作业完成率(%)
     */
    private Double assignmentCompletion;
    
    /**
     * 考试平均分
     */
    private Double examAvgScore;
    
    /**
     * 讨论参与次数
     */
    private Integer discussionCount;
    
    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 知识掌握情况(JSON)
     */
    private String knowledgeMastery;
    
    /**
     * 兴趣点数据(JSON)
     */
    private String interestPoints;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 