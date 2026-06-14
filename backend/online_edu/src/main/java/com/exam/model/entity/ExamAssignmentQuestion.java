package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 作业题目关联实体类
 */
@Data
@TableName("exam_assignment_question")
public class ExamAssignmentQuestion {
    
    /**
     * 作业ID
     */
    @TableId(value = "assignment_id", type = IdType.INPUT)
    private Long assignmentId;
    
    /**
     * 题目ID
     */
    @TableField("question_id")
    private Long questionId;
    
    /**
     * 题目顺序
     */
    @TableField("question_order")
    private Integer questionOrder;
    
    /**
     * 分值
     */
    private BigDecimal points;
    
    /**
     * 非数据库字段：题目内容
     */
    @TableField(exist = false)
    private ExamQuestionBank question;
} 