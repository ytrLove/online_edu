package com.stat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 学习概要请求对象
 */
@Data
public class LearningSummaryRequest {
    
    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
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
     * 知识掌握情况(JSON)
     */
    private String knowledgeMastery;
    
    /**
     * 兴趣点数据(JSON)
     */
    private String interestPoints;
} 