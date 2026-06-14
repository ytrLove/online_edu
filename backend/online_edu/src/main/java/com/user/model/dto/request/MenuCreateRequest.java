package com.user.model.dto.request;

import lombok.Data;

/**
 * 菜单创建请求DTO
 */
@Data
public class MenuCreateRequest {
    
    private String menuName;
    
    private Long parentId;
    
    private String path;
    
    private String component;
    
    private String perms;
    
    private String icon;
    
    private String menuType;
    
    private Integer orderNum;
    
    private Integer status;
    
    private String remark;
    
    private Boolean isButton;
} 