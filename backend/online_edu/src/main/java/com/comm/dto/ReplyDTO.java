package com.comm.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 讨论回复DTO
 */
@Data
public class ReplyDTO {
    
    /**
     * 回复ID
     */
    private Long replyId;
    
    /**
     * 主题ID
     */
    private Long topicId;
    
    /**
     * 回复内容
     */
    private String content;
    
    /**
     * 创建者ID
     */
    private Long userId;
    
    /**
     * 创建者用户名
     */
    private String username;
    
    /**
     * 创建者头像
     */
    private String avatar;
    
    /**
     * 父回复ID
     */
    private Long parentId;
    
    /**
     * 子回复列表
     */
    private List<ReplyDTO> childReplies;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 