package com.exam.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试记录视图对象
 */
@Data
public class ExamRecordVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 记录ID
     */
    private Long recordId;
    
    /**
     * 试卷ID
     */
    private Long examId;
    
    /**
     * 试卷标题
     */
    private String examTitle;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 状态（not_started未开始 in_progress进行中 submitted已提交 graded已评分）
     */
    private String status;
    
    /**
     * 总得分
     */
    private BigDecimal totalScore;
    
    /**
     * 试卷总分
     */
    private BigDecimal totalPoints;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 答案列表
     */
    private List<ExamQuestionVO> answers;
    
    /**
     * 剩余时间（秒）
     */
    private Long remainingTime;
} 