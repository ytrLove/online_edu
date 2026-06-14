package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_section_progress")
public class EduSectionProgress {
    @TableId(value = "progress_id", type = IdType.AUTO)
    private Long progressId;

    @TableField("student_id")
    private Long studentId;

    @TableField("section_id")
    private Long sectionId;

    private Integer progress;

    @TableField("completion_status")
    private String completionStatus;

    @TableField("last_position")
    private Integer lastPosition;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
    
    // 非数据库字段，用于显示学生信息
    @TableField(exist = false)
    private String studentName;
    
    // 非数据库字段，用于显示小节信息
    @TableField(exist = false)
    private String sectionTitle;
} 