package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_teaching_resource")
public class EduTeachingResource {
    @TableId(value = "resource_id", type = IdType.AUTO)
    private Long resourceId;

    @TableField("resource_name")
    private String resourceName;

    @TableField("resource_type_id")
    private Long resourceTypeId;

    @TableField("file_url")
    private String fileUrl;

    @TableField("file_size")
    private Long fileSize;

    @TableField("course_id")
    private Long courseId;

    @TableField("teacher_id")
    private Long teacherId;

    private String description;

    @TableField("is_public")
    private Integer isPublic;

    @TableField("download_count")
    private Integer downloadCount;

    private Integer status;

    @TableField("del_flag")
    private Integer delFlag;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String typeName;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String uploadByName;
} 