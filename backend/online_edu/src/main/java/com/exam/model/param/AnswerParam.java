package com.exam.model.param;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 答案请求参数
 */
@Data
public class AnswerParam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;
    
    /**
     * 学生答案
     */
    private String studentAnswer;
    
    /**
     * 得分（教师批改使用）
     */
    private BigDecimal score;
    
    /**
     * 反馈（教师批改使用）
     */
    private String feedback;
} 