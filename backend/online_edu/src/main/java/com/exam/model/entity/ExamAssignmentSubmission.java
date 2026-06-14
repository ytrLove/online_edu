package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业提交实体类
 */
@Data
@TableName("exam_assignment_submission")
public class ExamAssignmentSubmission {
    
    /**
     * 提交ID
     */
    @TableId(value = "submission_id", type = IdType.AUTO)
    private Long submissionId;
    
    /**
     * 作业ID
     */
    @TableField("assignment_id")
    private Long assignmentId;
    
    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;
    
    /**
     * 提交时间
     */
    @TableField("submission_time")
    private LocalDateTime submissionTime;
    
    /**
     * 状态（submitted已提交 grading评分中 graded已评分）
     */
    private String status;
    
    /**
     * 总分
     */
    @TableField("total_score")
    private BigDecimal totalScore;
    
    /**
     * 反馈
     */
    private String feedback;
    
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
     * 非数据库字段：学生姓名
     */
    @TableField(exist = false)
    private String studentName;
    
    /**
     * 非数据库字段：作业标题
     */
    @TableField(exist = false)
    private String assignmentTitle;
    
    /**
     * 非数据库字段：答案列表
     */
    @TableField(exist = false)
    private List<ExamAssignmentAnswer> answers;
} 