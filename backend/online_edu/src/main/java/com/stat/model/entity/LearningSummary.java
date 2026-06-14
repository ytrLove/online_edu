package com.stat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习情况汇总实体类
 */
@Data
@TableName("stat_learning_summary")
public class LearningSummary {

    /**
     * 汇总ID
     */
    @TableId(type = IdType.AUTO)
    private Long summaryId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 总学习时长（分钟）
     */
    private Integer totalStudyTime;

    /**
     * 考试平均分
     */
    private Double examAvgScore;

    /**
     * 作业完成率（百分比）
     */
    private Double assignmentCompletion;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 资源访问次数
     */
    private Integer resourceAccessCount;

    /**
     * 讨论参与次数
     */
    private Integer discussionCount;

    /**
     * 知识掌握情况(JSON)
     */
    private String knowledgeMastery;

    /**
     * 兴趣点数据(JSON)
     */
    private String interestPoints;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 学生姓名（关联字段）
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 课程名称（关联字段）
     */
    @TableField(exist = false)
    private String courseName;
}