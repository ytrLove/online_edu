package com.stat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习行为记录实体类
 */
@Data
@TableName("stat_learning_behavior")
public class LearningBehavior {

    /**
     * 行为ID
     */
    @TableId(type = IdType.AUTO)
    private Long behaviorId;

    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 行为类型
     * (VIDEO_WATCH观看视频/RESOURCE_DOWNLOAD资源下载/ASSIGNMENT_SUBMIT提交作业/FORUM_PARTICIPATE论坛参与/QUIZ_COMPLETE完成测验)
     */
    private String behaviorType;

    /**
     * 资源ID
     */
    private Long resourceId;
    
    /**
     * 行为描述
     */
    @TableField(exist = false)
    private String description;

    /**
     * 持续时间(分钟)
     */
    private Integer duration;
    
    /**
     * 完成率(0-1)
     */
    @TableField(exist = false)
    private Double completionRate;
    
    /**
     * IP地址
     */
    @TableField(exist = false)
    private String ipAddress;

    /**
     * 行为时间
     */
    private LocalDateTime behaviorTime;

    /**
     * 客户端信息
     */
    private String clientInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}