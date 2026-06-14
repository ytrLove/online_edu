package com.stat.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习行为数据传输对象
 */
@Data
public class LearningBehaviorDTO {
    
    /**
     * 行为ID
     */
    private Long behaviorId;
    
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
     * 行为类型
     */
    private String behaviorType;
    
    /**
     * 资源ID
     */
    private Long resourceId;
    
    /**
     * 资源名称
     */
    private String resourceName;
    
    /**
     * 持续时间(秒)
     */
    private Integer duration;
    
    /**
     * 行为时间
     */
    private LocalDateTime behaviorTime;
    
    /**
     * 客户端信息
     */
    private String clientInfo;
} 