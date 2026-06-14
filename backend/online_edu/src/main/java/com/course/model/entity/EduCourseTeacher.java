package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_course_teacher")
public class EduCourseTeacher {
    @TableId(value = "course_id", type = IdType.INPUT)
    private Long courseId;

    @TableField("teacher_id")
    private Long teacherId;

    private String role;

    @TableField("create_time")
    private LocalDateTime createTime;

    // 非数据库字段
    @TableField(exist = false)
    private String teacherName;
} 