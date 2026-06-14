package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("edu_course")
public class EduCourse {
    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;

    @TableField("course_name")
    private String courseName;

    @TableField("course_code")
    private String courseCode;

    @TableField("subject_id")
    private Long subjectId;

    private String description;

    private String objectives;

    private String syllabus;

    @TableField("cover_image")
    private String coverImage;

    private String status;

    @TableField("enrollment_start_time")
    private LocalDateTime enrollmentStartTime;

    @TableField("enrollment_end_time")
    private LocalDateTime enrollmentEndTime;

    @TableField("enrollment_status")
    private String enrollmentStatus; // open, closed, upcoming

    @TableField("del_flag")
    private Integer delFlag;


    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    private String remark;

    // 非数据库字段
    @TableField(exist = false)
    private String subjectName;

    @TableField(exist = false)
    private List<Long> teacherIds;

    @TableField(exist = false)
    private Integer studentCount;

    @TableField(exist = false)
    private String teacherName;
} 