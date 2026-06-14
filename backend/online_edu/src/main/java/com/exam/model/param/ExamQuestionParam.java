package com.exam.model.param;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 考试题目请求参数
 */
@Data
public class ExamQuestionParam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;
    
    /**
     * 题目顺序
     */
    private Integer questionOrder;
    
    /**
     * 分值
     */
    @DecimalMin(value = "0.0", message = "分值不能小于0")
    private BigDecimal points;
} 