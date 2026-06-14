package com.exam.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业视图对象
 */
@Data
public class AssignmentVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 作业ID
     */
    private Long assignmentId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
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
    private LocalDateTime startTime;
    
    /**
     * 截止时间
     */
    private LocalDateTime dueTime;
    
    /**
     * 总分
     */
    private BigDecimal totalPoints;
    
    /**
     * 创建者ID（教师）
     */
    private Long teacherId;
    
    /**
     * 教师名称
     */
    private String teacherName;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 题目列表
     */
    private List<AssignmentQuestionVO> questions;
    
    /**
     * 提交信息 (仅学生查看作业时返回)
     */
    private SubmissionVO submission;

    /**
     * 总人数
     */
    private Integer totalCount;

    /**
     * 已提交人数
     */
    private Integer submittedCount;
} 