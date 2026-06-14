package com.exam.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业提交视图对象
 */
@Data
public class SubmissionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 提交ID
     */
    private Long submissionId;
    
    /**
     * 作业ID
     */
    private Long assignmentId;
    
    /**
     * 作业标题
     */
    private String assignmentTitle;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 提交时间
     */
    private LocalDateTime submissionTime;
    
    /**
     * 状态（submitted已提交 grading评分中 graded已评分）
     */
    private String status;
    
    /**
     * 总分
     */
    private BigDecimal totalScore;
    
    /**
     * 反馈
     */
    private String feedback;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
    
    /**
     * 答案列表
     */
    private List<AssignmentQuestionVO> answers;
} 