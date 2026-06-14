package com.exam.model.vo;

import com.exam.model.entity.ExamQuestionBank;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生作业视图对象
 */
@Data
public class StudentAssignmentVO {
    /**
     * 作业ID
     */
    private Long assignmentId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 作业标题
     */
    private String title;
    
    /**
     * 作业描述
     */
    private String description;
    
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueTime;
    
    /**
     * 总分
     */
    private BigDecimal totalPoints;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
    
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer delFlag;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 教师名称
     */
    private String teacherName;
    
    /**
     * 题目列表
     */
    private List<ExamQuestionVO> questions;
    
    /**
     * 提交状态：未提交(not_submitted)、已提交(submitted)、已批改(graded)
     */
    private String submissionStatus;
    
    /**
     * 提交ID
     */
    private Long submissionId;
    
    /**
     * 得分
     */
    private BigDecimal score;
    
    /**
     * 截止日期状态：未开始(not_started)、进行中(in_progress)、已截止(expired)
     */
    private String deadlineStatus;
    
    /**
     * 剩余时间（小时）
     */
    private Long remainingHours;
} 