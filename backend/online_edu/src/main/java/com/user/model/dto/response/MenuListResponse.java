package com.user.model.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 菜单列表响应DTO
 */
@Data
public class MenuListResponse {
    
    private Long total;
    
    private List<MenuInfoResponse> rows;
} 