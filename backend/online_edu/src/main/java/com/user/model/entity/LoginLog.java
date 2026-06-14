package com.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private LocalDateTime loginTime;

    private String ipAddress;

    private String location;

    private String browser;

    private String os;

    private Integer status;

    private String message;

    // 用于接收前端参数，不存入数据库
    private transient LocalDateTime startTime;
    private transient LocalDateTime endTime;
}
