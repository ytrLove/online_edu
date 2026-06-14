package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试记录实体类
 */
@Data
@TableName("exam_record")
public class ExamRecord {
    
    /**
     * 记录ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;
    
    /**
     * 试卷ID
     */
    @TableField("exam_id")
    private Long examId;
    
    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;
    
    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;
    
    /**
     * 提交时间
     */
    @TableField("submit_time")
    private LocalDateTime submitTime;
    
    /**
     * 状态（not_started未开始 in_progress进行中 submitted已提交 graded已评分）
     */
    @TableField("status")
    private String status;
    
    /**
     * 总得分
     */
    @TableField("total_score")
    private BigDecimal totalScore;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 非数据库字段：学生姓名
     */
    @TableField(exist = false)
    private String studentName;
    
    /**
     * 非数据库字段：考试标题
     */
    @TableField(exist = false)
    private String examTitle;
    
    /**
     * 非数据库字段：课程名称
     */
    @TableField(exist = false)
    private String courseName;
    
    /**
     * 非数据库字段：试卷总分
     */
    @TableField(exist = false)
    private BigDecimal totalPoints;
    
    /**
     * 非数据库字段：答案列表
     */
    @TableField(exist = false)
    private List<ExamAnswer> answers;
    
    /**
     * 试卷信息（非数据库字段）
     */
    @TableField(exist = false)
    private ExamPaper exam;
} 