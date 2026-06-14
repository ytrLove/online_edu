package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 试卷题目关联实体类
 */
@Data
@TableName("exam_paper_question")
public class ExamPaperQuestion {
    
    /**
     * 试卷ID
     */
    @TableId(value = "exam_id", type = IdType.INPUT)
    private Long examId;
    
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