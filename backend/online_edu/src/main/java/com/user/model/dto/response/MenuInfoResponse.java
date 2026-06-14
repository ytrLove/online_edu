package com.user.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单信息响应DTO
 */
@Data
public class MenuInfoResponse {
    
    /**
     * 菜单ID
     */
    private Long menuId;
    
    /**
     * 菜单名称
     */
    private String menuName;
    
    /**
     * 父菜单ID
     */
    private Long parentId;
    
    /**
     * 路由地址
     */
    private String path;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 子菜单列表
     */
    private List<MenuInfoResponse> children;
    
    /**
     * 权限标识
     */
    private String perms;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 菜单类型（M:目录 C:菜单 F:按钮）
     */
    private String menuType;
    
    /**
     * 显示顺序
     */
    private Integer orderNum;
    
    /**
     * 菜单状态（0:禁用 1:正常）
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
     * 是否为按钮
     */
    private Boolean isButton;

} 