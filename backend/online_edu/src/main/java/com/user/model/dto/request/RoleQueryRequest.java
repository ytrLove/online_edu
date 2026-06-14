package com.user.model.dto.request;

import lombok.Data;

/**
 * 角色查询请求DTO
 */
@Data
public class RoleQueryRequest {
    
    private Long pageNum = 1L;
    
    private Long pageSize = 10L;
    
    private String roleName;
    
    private String roleKey;
    
    private Integer status;
} 