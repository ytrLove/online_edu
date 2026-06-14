package com.stat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学报告实体类
 */
@Data
@TableName("stat_teaching_report")
public class TeachingReport {

    /**
     * 报告ID
     */
    @TableId(type = IdType.AUTO)
    private Long reportId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 报告类型(course课程/teacher教师/student_group学生群体)
     */
    private String reportType;

    /**
     * 报告周期(weekly周/monthly月/term学期)
     */
    private String reportPeriod;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

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