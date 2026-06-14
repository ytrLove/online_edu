package com.user.model.entity;



import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_operation_logs")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;       // 操作模块
    private String method;      // 请求方法
    private String requestMethod; // 请求方式
    private String operatorType; // 操作类型（新增、修改、删除等）
    private String operName;    // 操作人员
    private String operUrl;     // 请求URL
    private String operIp;      // 主机地址
    private String operLocation; // 操作地点
    private String operParam;   // 请求参数
    private String jsonResult;  // 返回参数
    private Integer status;     // 操作状态（0正常 1异常）
    private String errorMsg;    // 错误消息

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}
