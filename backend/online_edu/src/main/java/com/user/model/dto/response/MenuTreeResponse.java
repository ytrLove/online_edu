package com.user.model.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 菜单树响应DTO
 */
@Data
public class MenuTreeResponse {
    
    private List<MenuInfoResponse> menus;
    
    private List<Long> checkedKeys;
} 