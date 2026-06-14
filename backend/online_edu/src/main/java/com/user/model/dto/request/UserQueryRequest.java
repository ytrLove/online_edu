package com.user.model.dto.request;

import lombok.Data;

/**
 * 用户查询请求DTO
 */
@Data
public class UserQueryRequest {
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户状态（0:禁用, 1:启用）
     */
    private Integer status;
} 