package com.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@Data
@TableName("sys_scheduled_task_log")
public class ScheduledTaskLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;
    private Date startTime;
    private Date endTime;
    private Integer executionStatus;
    private String errorMessage;
    private String executionResult;
}
