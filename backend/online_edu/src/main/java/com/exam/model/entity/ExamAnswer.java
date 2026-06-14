package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试答案实体类
 */
@Data
@TableName("exam_answer")
public class ExamAnswer {
    
    /**
     * 记录ID
     */
    @TableId(value = "record_id", type = IdType.INPUT)
    private Long recordId;
    
    /**
     * 题目ID
     */
    @TableField("question_id")
    private Long questionId;
    
    /**
     * 学生答案
     */
    @TableField("student_answer")
    private String studentAnswer;
    
    /**
     * 得分
     */
    private BigDecimal score;
    
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
     * 非数据库字段：题目内容
     */
    @TableField(exist = false)
    private String questionContent;
    
    /**
     * 非数据库字段：正确答案
     */
    @TableField(exist = false)
    private String correctAnswer;
    
    /**
     * 非数据库字段：题目选项
     */
    @TableField(exist = false)
    private String options;
    
    /**
     * 非数据库字段：题目类型名称
     */
    @TableField(exist = false)
    private String typeName;
    
    /**
     * 非数据库字段：题目分值
     */
    @TableField(exist = false)
    private BigDecimal points;
    
    /**
     * 非数据库字段：题目信息
     */
    @TableField(exist = false)
    private ExamQuestionBank question;
} 