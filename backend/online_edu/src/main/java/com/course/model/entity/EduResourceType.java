package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_resource_type")
public class EduResourceType {
    @TableId(value = "type_id", type = IdType.AUTO)
    private Long typeId;

    @TableField("type_name")
    private String typeName;

    @TableField("file_extensions")
    private String fileExtensions;

    private String icon;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    private String remark;
} 