package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业实体类
 */
@Data
@TableName("exam_assignment")
public class ExamAssignment {
    
    /**
     * 作业ID
     */
    @TableId(value = "assignment_id", type = IdType.AUTO)
    private Long assignmentId;
    
    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;
    
    /**
     * 截止时间
     */
    @TableField("due_time")
    private LocalDateTime dueTime;
    
    /**
     * 总分
     */
    @TableField("total_points")
    private BigDecimal totalPoints;
    
    /**
     * 创建者ID（教师）
     */
    @TableField("teacher_id")
    private Long teacherId;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
    
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableField("del_flag")
    @TableLogic
    private Integer delFlag;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 非数据库字段：课程名称
     */
    @TableField(exist = false)
    private String courseName;
    
    /**
     * 非数据库字段：教师名称
     */
    @TableField(exist = false)
    private String teacherName;
    
    /**
     * 非数据库字段：关联的题目
     */
    @TableField(exist = false)
    private List<ExamQuestionBank> questions;
} 