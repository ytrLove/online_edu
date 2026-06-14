package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_section")
public class EduSection {
    @TableId(value = "section_id", type = IdType.AUTO)
    private Long sectionId;

    @TableField("chapter_id")
    private Long chapterId;

    private String title;

    private String content;

    @TableField("video_url")
    private String videoUrl;

    private Integer duration;

    @TableField("image_url")
    private String imageUrl;

    @TableField("resource_type")
    private String resourceType;

    @TableField("free_preview")
    private Boolean freePreview;

    private Integer sort;

    private Integer status;

    @TableField("del_flag")
    private Integer delFlag;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    private String remark;
    
    // 非数据库字段，用于显示章节标题
    @TableField(exist = false)
    private String chapterTitle;
} 