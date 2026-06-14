package com.user.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 角色信息响应DTO
 */
@Data
public class RoleInfoResponse {
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色权限字符串
     */
    private String roleKey;
    
    /**
     * 角色排序
     */
    private Integer roleSort;
    
    /**
     * 角色状态（0:停用, 1:正常）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 权限列表
     */
    private Set<String> permissions;
    
    /**
     * 菜单列表
     */
    private List<MenuInfoResponse> menus;
} 