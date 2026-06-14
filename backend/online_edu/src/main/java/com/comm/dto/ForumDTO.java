package com.comm.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 讨论区DTO
 */
@Data
public class ForumDTO {
    
    /**
     * 讨论区ID
     */
    private Long forumId;
    
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
    private Integer isPublic;
    
    /**
     * 状态（1正常，0停用）
     */
    private Integer status;
    
    /**
     * 课程名称
     */
    private String courseName;
    
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
} 