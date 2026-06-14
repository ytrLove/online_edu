package com.stat.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学报告数据传输对象
 */
@Data
public class TeachingReportDTO {
    
    /**
     * 报告ID
     */
    private Long reportId;
    
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
     * 报告类型(course课程/teacher教师/student_group学生群体)
     */
    private String reportType;
    
    /**
     * 报告周期(weekly周/monthly月/term学期)
     */
    private String reportPeriod;
    
    /**
     * 报告数据(JSON)
     */
    private String reportData;
    
    /**
     * 教学洞察(JSON)
     */
    private String insights;
    
    /**
     * 改进领域(JSON)
     */
    private String improvementAreas;
    
    /**
     * 生成时间
     */
    private LocalDateTime generateTime;
} 