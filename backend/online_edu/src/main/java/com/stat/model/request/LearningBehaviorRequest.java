package com.stat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 学习行为请求对象
 */
@Data
public class LearningBehaviorRequest {
    
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
     * 行为类型
     */
    @NotNull(message = "行为类型不能为空")
    private String behaviorType;
    
    /**
     * 资源ID
     */
    private Long resourceId;
    
    /**
     * 持续时间(秒)
     */
    private Integer duration;
    
    /**
     * 客户端信息
     */
    private String clientInfo;
} 