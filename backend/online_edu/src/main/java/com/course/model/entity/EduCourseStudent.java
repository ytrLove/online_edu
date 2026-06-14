package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("edu_course_student")
public class EduCourseStudent {
    @TableId(value = "course_id", type = IdType.INPUT)
    private Long courseId;

    @TableField("student_id")
    private Long studentId;

    @TableField("enrollment_date")
    private LocalDateTime enrollmentDate;

    @TableField("completion_status")
    private String completionStatus;

    @TableField("final_grade")
    private BigDecimal finalGrade;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String studentName;

    @TableField(exist = false)
    private String courseName;
} 