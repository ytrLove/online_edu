package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论回复查询请求参数
 */
@Data
public class ReplyQueryRequest {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 20;
    
    /**
     * 主题ID
     */
    private Long topicId;
    
    /**
     * 父回复ID
     */
    private Long parentId;
} 