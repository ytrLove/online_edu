package com.stat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学评价实体类
 */
@Data
@TableName("stat_teaching_evaluation")
public class TeachingEvaluation {

    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long evaluationId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 评价人ID(学生)
     */
    private Long studentId;

    /**
     * 教学质量评分(1-5分)
     */
    private Double teachingQuality;

    /**
     * 内容质量评分(1-5分)
     */
    private Double contentQuality;

    /**
     * 互动评分(1-5分)
     */
    private Double interactionScore;

    /**
     * 资源质量评分(1-5分)
     */
    private Double resourceQuality;

    /**
     * 总体评分(1-5分)
     */
    private Double overallScore;

    /**
     * 改进建议
     */
    private String improvementSuggest;

    /**
     * 评价时间
     */
    private LocalDateTime evaluateTime;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String teacherName;

    @TableField(exist = false)
    private String studentName;
}