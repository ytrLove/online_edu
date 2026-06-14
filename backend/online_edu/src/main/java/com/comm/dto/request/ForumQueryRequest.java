package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论区查询请求参数
 */
@Data
public class ForumQueryRequest {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 讨论区名称
     */
    private String forumName;
    
    /**
     * 是否公开（0否 1是）
     */
    private Integer isPublic;
} 