package com.user.model.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 角色创建请求DTO
 */
@Data
public class RoleCreateRequest {
    
    private String roleName;
    
    private String roleKey;
    
    private Integer roleSort;
    
    private Integer status;
    
    private String remark;
    
    private List<Long> menuIds;
} 