package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("edu_chapter")
public class EduChapter {
    @TableId(value = "chapter_id", type = IdType.AUTO)
    private Long chapterId;

    @TableField("course_id")
    private Long courseId;

    private String title;

    private Integer sort;

    private Integer status;

    @TableField("del_flag")
    private Integer delFlag;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    private String remark;
    
    // 非数据库字段，用于存放章节下的小节列表
    @TableField(exist = false)
    private List<EduSection> sections;
    
    // 非数据库字段，用于显示课程名称
    @TableField(exist = false)
    private String courseName;
} 