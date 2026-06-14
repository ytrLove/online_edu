package com.user.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息响应DTO
 */
@Data
public class UserInfoResponse {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别（0:未知, 1:男, 2:女）
     */
    private String sex;
    
    /**
     * 头像地址
     */
    private String avatar;
    
    /**
     * 用户状态（0:禁用, 1:启用）
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
     * 角色信息列表
     */
    private List<RoleInfoResponse> roles;
} 