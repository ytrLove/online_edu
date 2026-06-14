package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_teacher")
public class EduTeacher {
    @TableId(value = "teacher_id", type = IdType.INPUT)
    private Long teacherId;

    @TableField("user_id")
    private Long userId;

    @TableField("teacher_code")
    private String teacherCode;

    private String title;

    private String department;

    @TableField("research_field")
    private String researchField;

    private String education;

    private String degree;

    @TableField("teaching_years")
    private Integer teachingYears;

    @TableField("teacher_status")
    private String teacherStatus;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
    
    private String remark;

    // 关联用户表的字段
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String email;

    @TableField(exist = false)
    private String phone;

    @TableField(exist = false)
    private String avatar;
} 