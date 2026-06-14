package com.exam.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试试卷视图对象
 */
@Data
public class ExamPaperVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 试卷ID
     */
    private Long examId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 教师名称
     */
    private String teacherName;
    
    /**
     * 试卷标题
     */
    private String title;
    
    /**
     * 试卷描述
     */
    private String description;
    
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 考试时长（分钟）
     */
    private Integer duration;
    
    /**
     * 总分
     */
    private BigDecimal totalPoints;
    
    /**
     * 状态（0未发布 1已发布）
     */
    private Integer status;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 题目列表
     */
    private List<ExamQuestionVO> questions;
    
    /**
     * 考试记录ID（学生视图）
     */
    private Long recordId;
    
    /**
     * 学生开始考试时间（学生视图）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startExamTime;
    
    /**
     * 学生结束考试时间（学生视图）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endExamTime;
    
    /**
     * 学生分数（学生视图）
     */
    private BigDecimal studentScore;
    
    /**
     * 是否已提交（学生视图）
     */
    private Boolean submitted;
    
    /**
     * 是否已批改（学生视图）
     */
    private Boolean graded;
} 