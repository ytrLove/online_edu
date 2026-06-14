package com.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

// ScheduledTask.java
@Data
@TableName("sys_scheduled_task")
public class ScheduledTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;
    private String taskDescription;
    private String taskClass;
    private String taskMethod;
    private String cronExpression;
    private String params;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}

