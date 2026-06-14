package com.exam.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 作业题目视图对象
 */
@Data
public class AssignmentQuestionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 作业ID
     */
    private Long assignmentId;
    
    /**
     * 题目ID
     */
    private Long questionId;
    
    /**
     * 题目顺序
     */
    private Integer questionOrder;
    
    /**
     * 分值
     */
    private BigDecimal points;
    
    /**
     * 题目信息
     */
    private QuestionVO question;
    
    /**
     * 学生答案 (仅学生查看作业时返回)
     */
    private String studentAnswer;
    
    /**
     * 得分 (仅批改后返回)
     */
    private BigDecimal score;
    
    /**
     * 反馈 (仅批改后返回)
     */
    private String feedback;
} 