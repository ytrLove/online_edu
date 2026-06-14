package com.stat.model.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 教学评价请求对象
 */
@Data
public class TeachingEvaluationRequest {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;
    
    /**
     * 教学质量评分(1-5分)
     */
    @NotNull(message = "教学质量评分不能为空")
    @DecimalMin(value = "1.0", message = "评分不能小于1分")
    @DecimalMax(value = "5.0", message = "评分不能大于5分")
    private Double teachingQuality;
    
    /**
     * 内容质量评分(1-5分)
     */
    @NotNull(message = "内容质量评分不能为空")
    @DecimalMin(value = "1.0", message = "评分不能小于1分")
    @DecimalMax(value = "5.0", message = "评分不能大于5分")
    private Double contentQuality;
    
    /**
     * 互动评分(1-5分)
     */
    @NotNull(message = "互动评分不能为空")
    @DecimalMin(value = "1.0", message = "评分不能小于1分")
    @DecimalMax(value = "5.0", message = "评分不能大于5分")
    private Double interactionScore;
    
    /**
     * 资源质量评分(1-5分)
     */
    @NotNull(message = "资源质量评分不能为空")
    @DecimalMin(value = "1.0", message = "评分不能小于1分")
    @DecimalMax(value = "5.0", message = "评分不能大于5分")
    private Double resourceQuality;
    
    /**
     * 总体评分(1-5分)
     */
    @NotNull(message = "总体评分不能为空")
    @DecimalMin(value = "1.0", message = "评分不能小于1分")
    @DecimalMax(value = "5.0", message = "评分不能大于5分")
    private Double overallScore;
    
    /**
     * 改进建议
     */
    private String improvementSuggest;
} 