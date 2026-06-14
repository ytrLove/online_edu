package com.stat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 教学报告请求对象
 */
@Data
public class TeachingReportRequest {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 报告类型(course课程/teacher教师/student_group学生群体)
     */
    @NotNull(message = "报告类型不能为空")
    private String reportType;
    
    /**
     * 报告周期(weekly周/monthly月/term学期)
     */
    @NotNull(message = "报告周期不能为空")
    private String reportPeriod;
} 