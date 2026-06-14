package com.exam.model.param;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 题目请求参数
 */
@Data
public class QuestionParam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 题目ID（修改时必填）
     */
    private Long questionId;
    
    /**
     * 题目类型ID
     */
    @NotNull(message = "题目类型不能为空")
    private Long questionTypeId;
    
    /**
     * 题目内容
     */
    @NotBlank(message = "题目内容不能为空")
    private String questionContent;
    
    /**
     * 选项（JSON格式）
     */
    private String options;
    
    /**
     * 正确答案
     */
    private String correctAnswer;
    
    /**
     * 解析
     */
    private String explanation;
    
    /**
     * 难度（easy简单 medium中等 hard困难）
     */
    private String difficulty;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
} 