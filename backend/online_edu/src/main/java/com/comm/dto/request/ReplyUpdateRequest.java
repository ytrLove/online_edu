package com.comm.dto.request;

import lombok.Data;

/**
 * 讨论回复更新请求参数
 */
@Data
public class ReplyUpdateRequest {
    
    /**
     * 回复ID
     */
    private Long replyId;
    
    /**
     * 回复内容
     */
    private String content;
} 