package com.user.model.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 分配角色请求DTO
 */
@Data
public class AssignRolesRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
} 