package com.exam.model.param;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 作业题目关联参数
 */
@Data
public class AssignmentQuestionParam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 作业ID
     */
    @NotNull(message = "作业ID不能为空")
    private Long assignmentId;
    
    /**
     * 题目列表
     */
    private List<QuestionDetail> questions;
    
    /**
     * 题目详情
     */
    @Data
    public static class QuestionDetail implements Serializable {
        
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
        private BigDecimal points;
    }
} 