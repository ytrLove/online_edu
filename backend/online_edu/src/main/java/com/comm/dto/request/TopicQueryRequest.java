package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论主题查询请求参数
 */
@Data
public class TopicQueryRequest {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 讨论区ID
     */
    private Long forumId;
    
    /**
     * 主题标题
     */
    private String title;
    
    /**
     * 创建者ID
     */
    private Long userId;
    
    /**
     * 是否置顶（0否 1是）
     */
    private Integer isPinned;
} 