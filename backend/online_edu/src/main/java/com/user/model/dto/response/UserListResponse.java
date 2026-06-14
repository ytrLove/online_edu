package com.user.model.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 用户列表响应DTO
 */
@Data
public class UserListResponse {
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 用户列表
     */
    private List<UserInfoResponse> rows;
} 