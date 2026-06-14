package com.user.model.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 角色列表响应DTO
 */
@Data
public class RoleListResponse {
    
    private Long total;
    
    private List<RoleDetailResponse> rows;
} 