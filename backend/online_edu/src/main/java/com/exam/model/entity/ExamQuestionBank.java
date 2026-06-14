package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 题库实体类
 */
@Data
@TableName("exam_question_bank")
public class ExamQuestionBank {
    
    /**
     * 题目ID
     */
    @TableId(value = "question_id", type = IdType.AUTO)
    private Long questionId;
    
    /**
     * 题目类型ID
     */
    @TableField("question_type_id")
    private Long questionTypeId;
    
    /**
     * 题目内容
     */
    @TableField("question_content")
    private String questionContent;
    
    /**
     * 题目名称（简短显示用）
     */
    @TableField(exist = false)
    private String questionName;
    
    /**
     * 选项（JSON格式）
     */
    private String options;
    
    /**
     * 正确答案
     */
    @TableField("correct_answer")
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
    @TableField("course_id")
    private Long courseId;
    
    /**
     * 创建者ID（教师）
     */
    @TableField("teacher_id")
    private Long teacherId;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
    
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableField("del_flag")
    @TableLogic
    private Integer delFlag;
    
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
     * 非数据库字段：题型名称
     */
    @TableField(exist = false)
    private String typeName;
    
    /**
     * 非数据库字段：课程名称
     */
    @TableField(exist = false)
    private String courseName;
    
    /**
     * 非数据库字段：教师名称
     */
    @TableField(exist = false)
    private String teacherName;
    
    /**
     * 非数据库字段：是否自动评分
     */
    @TableField(exist = false)
    private Boolean isAutoGraded;
} 