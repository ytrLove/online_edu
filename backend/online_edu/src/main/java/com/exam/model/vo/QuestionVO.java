package com.exam.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题目视图对象
 */
@Data
public class QuestionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 题目ID
     */
    private Long questionId;
    
    /**
     * 题目类型ID
     */
    private Long questionTypeId;
    
    /**
     * 题目类型名称
     */
    private String typeName;
    
    /**
     * 是否自动评分
     */
    private Boolean isAutoGraded;
    
    /**
     * 题目内容
     */
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
     * 课程名称
     */
    private String courseName;
    
    /**
     * 创建者ID（教师）
     */
    private Long teacherId;
    
    /**
     * 教师名称
     */
    private String teacherName;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 