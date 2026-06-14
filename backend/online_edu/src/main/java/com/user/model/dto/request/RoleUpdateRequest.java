package com.user.model.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 角色更新请求DTO
 */
@Data
public class RoleUpdateRequest {
    
    private Long roleId;
    
    private String roleName;
    
    private String roleKey;
    
    private Integer roleSort;
    
    private Integer status;
    
    private String remark;
    
    private List<Long> menuIds;
} 