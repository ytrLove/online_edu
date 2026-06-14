package com.exam.model.param;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * 作业提交请求参数
 */
@Data
public class SubmissionParam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 提交ID（修改已有提交时使用）
     */
    private Long submissionId;
    
    /**
     * 作业ID
     */
    @NotNull(message = "作业ID不能为空")
    private Long assignmentId;
    
    /**
     * 答案列表
     */
    @Valid
    private List<AnswerParam> answers;
} 