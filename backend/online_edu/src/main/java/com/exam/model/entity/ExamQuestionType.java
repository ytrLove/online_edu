package com.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 题目类型实体类
 */
@Data
@TableName("exam_question_type")
public class ExamQuestionType {
    
    /**
     * 类型ID
     */
    @TableId(value = "type_id", type = IdType.AUTO)
    private Long typeId;
    
    /**
     * 类型名称
     */
    @TableField("type_name")
    private String typeName;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 是否自动评分（0否 1是）
     */
    @TableField("is_auto_graded")
    private Integer isAutoGraded;
    
    /**
     * 状态（1正常 0停用）
     */
    private Integer status;
    
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
} 