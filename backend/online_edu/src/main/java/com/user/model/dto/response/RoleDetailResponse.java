package com.user.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色详情响应DTO
 */
@Data
public class RoleDetailResponse {
    
    private Long roleId;
    
    private String roleName;
    
    private String roleKey;
    
    private Integer roleSort;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String remark;
    
    private List<MenuInfoResponse> menus;
} 