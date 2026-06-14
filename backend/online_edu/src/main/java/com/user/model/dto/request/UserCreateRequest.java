package com.user.model.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 用户创建请求DTO
 * 包含创建新用户所需的所有字段
 */
@Data
public class UserCreateRequest {
    
    /**
     * 用户名
     * 必填，长度2-20个字符
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String username;
    
    /**
     * 密码
     * 必填，长度6-20个字符
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
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