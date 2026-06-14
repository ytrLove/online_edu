package com.user.model.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 分配菜单请求DTO
 */
@Data
public class AssignMenusRequest {
    
    private Long roleId;
    
    private List<Long> menuIds;
} 