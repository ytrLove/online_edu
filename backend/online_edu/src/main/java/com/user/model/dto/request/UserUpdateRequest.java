package com.user.model.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * 用户更新请求DTO
 * 包含更新用户所需的所有字段
 */
@Data
public class UserUpdateRequest {
    
    /**
     * 用户ID
     * 必填
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     * 需符合邮箱格式
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号
     * 需符合手机号格式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
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
     * 角色ID列表
     */
    private List<Long> roleIds;
    
    /**
     * 备注
     */
    private String remark;
} 