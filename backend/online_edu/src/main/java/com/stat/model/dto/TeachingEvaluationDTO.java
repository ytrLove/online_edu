package com.stat.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学评价数据传输对象
 */
@Data
public class TeachingEvaluationDTO {
    
    /**
     * 评价ID
     */
    private Long evaluationId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 教师姓名
     */
    private String teacherName;
    
    /**
     * 评价人ID(学生)
     */
    private Long studentId;
    
    /**
     * 评价人姓名
     */
    private String studentName;
    
    /**
     * 教学质量评分(1-5分)
     */
    private Double teachingQuality;
    
    /**
     * 内容质量评分(1-5分)
     */
    private Double contentQuality;
    
    /**
     * 互动评分(1-5分)
     */
    private Double interactionScore;
    
    /**
     * 资源质量评分(1-5分)
     */
    private Double resourceQuality;
    
    /**
     * 总体评分(1-5分)
     */
    private Double overallScore;
    
    /**
     * 改进建议
     */
    private String improvementSuggest;
    
    /**
     * 评价时间
     */
    private LocalDateTime evaluateTime;
} 