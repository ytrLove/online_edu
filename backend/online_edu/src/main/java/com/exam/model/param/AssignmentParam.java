package com.exam.model.param;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业请求参数
 */
@Data
public class AssignmentParam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 作业ID（修改时必填）
     */
    private Long assignmentId;
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 截止时间
     */
    @Future(message = "截止时间必须是未来时间")
    private LocalDateTime dueTime;
    
    /**
     * 总分
     */
    @DecimalMin(value = "0.0", message = "总分不能小于0")
    private BigDecimal totalPoints;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
    
    /**
     * 题目列表
     */
    @Valid
    private List<AssignmentQuestionParam.QuestionDetail> questions;
    
    /**
     * 作业内容
     */
    private String content;
    
    /**
     * 文件要求
     */
    private String fileRequirements;
    
    /**
     * 是否公开评分
     */
    private Boolean publicScore;
    
    /**
     * 题库ID列表，用于批量关联题目
     */
    private List<Long> questionIds;
} 