package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论区创建请求参数
 */
@Data
public class ForumCreateRequest {
    
    /**
     * 关联的课程ID
     */
    private Long courseId;
    
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
    private Integer isPublic = 0;
    
    /**
     * 状态（1正常，0停用）
     */
    private Integer status = 1;
    
    /**
     * 备注
     */
    private String remark;
} 