package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论区更新请求参数
 */
@Data
public class ForumUpdateRequest {
    
    /**
     * 讨论区ID
     */
    private Long forumId;
    
    /**
     * 讨论区名称
     */
    private String forumName;
    
    /**
     * 讨论区描述
     */
    private String description;
    
    /**
     * 是否公开（0否，1是）
     */
    private Integer isPublic;
    
    /**
     * 状态（1正常，0停用）
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
} 