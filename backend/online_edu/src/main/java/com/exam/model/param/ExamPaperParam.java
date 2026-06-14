package com.exam.model.param;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试试卷请求参数
 */
@Data
public class ExamPaperParam implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 试卷ID（修改时必填）
     */
    private Long examId;
    
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
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @Future(message = "结束时间必须是未来时间")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    /**
     * 考试时长（分钟）
     */
    @Min(value = 1, message = "考试时长不能小于1分钟")
    private Integer duration;
    
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
     * 创建者
     */
    private String createBy;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 题目列表
     */
    @Valid
    private List<ExamQuestionParam> questions;
} 