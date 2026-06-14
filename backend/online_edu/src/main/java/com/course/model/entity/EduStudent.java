package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("edu_student")
public class EduStudent {
    @TableId(value = "student_id", type = IdType.INPUT)
    private Long studentId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField("student_no")
    private String studentNo;

    @TableField("admission_date")
    private LocalDate admissionDate;

    @TableField("graduation_date")
    private LocalDate graduationDate;


    private String major;

    @TableField("class_name")
    private String className;

    /**
     * 学生状态：1-启用，0-禁用
     */
    @TableField("student_status")
    private String studentStatus;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
    
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