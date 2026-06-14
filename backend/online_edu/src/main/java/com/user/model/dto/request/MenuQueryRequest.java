package com.user.model.dto.request;

import lombok.Data;

/**
 * 菜单查询请求DTO
 */
@Data
public class MenuQueryRequest {
    
    private String menuName;
    
    private Integer status;
} 