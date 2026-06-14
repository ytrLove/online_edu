package com.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("edu_subject")
public class EduSubject {
    @TableId(value = "subject_id", type = IdType.AUTO)
    private Long subjectId;

    @TableField("subject_name")
    private String subjectName;

    @TableField("parent_id")
    private Long parentId;

    @TableField("order_num")
    private Integer orderNum;

    private Integer status;

    @TableField("del_flag")
    private Integer delFlag;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    private String remark;

    // 非数据库字段
    @TableField(exist = false)
    private List<EduSubject> children;
} 