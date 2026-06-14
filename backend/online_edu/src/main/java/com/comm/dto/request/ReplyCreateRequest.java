package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论回复创建请求参数
 */
@Data
public class ReplyCreateRequest {
    
    /**
     * 主题ID
     */
    private Long topicId;
    
    /**
     * 回复内容
     */
    private String content;
    
    /**
     * 父回复ID
     */
    private Long parentId;
} 