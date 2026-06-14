package com.exam.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试题目视图对象
 */
@Data
public class ExamQuestionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 试卷ID
     */
    private Long examId;
    
    /**
     * 题目ID
     */
    private Long questionId;
    
    /**
     * 题目序号
     */
    private Integer questionOrder;
    
    /**
     * 分值
     */
    private BigDecimal points;
    
    /**
     * 题目类型ID
     */
    private Long questionTypeId;
    
    /**
     * 题目类型名称
     */
    private String typeName;
    
    /**
     * 是否自动评分
     */
    private Boolean isAutoGraded;
    
    /**
     * 题目内容
     */
    private String content;
    
    /**
     * 题目名称（简短显示）
     */
    private String questionName;
    
    /**
     * 选项内容（选择题）
     */
    private String options;
    
    /**
     * 正确答案
     */
    private String answer;
    
    /**
     * 解析
     */
    private String analysis;
    
    /**
     * 难度（easy简单 medium中等 hard困难）
     */
    private String difficulty;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
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
     * 学生答案（学生视图）
     */
    private String studentAnswer;
    
    /**
     * 得分（学生视图）
     */
    private BigDecimal score;
    
    /**
     * 评语（学生视图）
     */
    private String feedback;
    
    /**
     * 是否答对（学生视图）
     */
    private Boolean isCorrect;
    
    /**
     * 是否已回答（学生视图）
     */
    private Boolean isAnswered;
} 