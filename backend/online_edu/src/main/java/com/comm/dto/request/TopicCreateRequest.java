package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论主题创建请求参数
 */
@Data
public class TopicCreateRequest {
    
    /**
     * 所属讨论区ID
     */
    private Long forumId;
    
    /**
     * 主题标题
     */
    private String title;
    
    /**
     * 主题内容
     */
    private String content;
} 