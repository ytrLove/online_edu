package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论主题更新请求参数
 */
@Data
public class TopicUpdateRequest {
    
    /**
     * 主题ID
     */
    private Long topicId;
    
    /**
     * 主题标题
     */
    private String title;
    
    /**
     * 主题内容
     */
    private String content;
    
    /**
     * 是否置顶（0否 1是）
     */
    private Integer isPinned;
} 