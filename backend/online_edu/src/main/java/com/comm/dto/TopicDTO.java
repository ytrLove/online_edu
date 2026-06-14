package com.comm.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 讨论主题DTO
 */
@Data
public class TopicDTO {
    
    /**
     * 主题ID
     */
    private Long topicId;
    
    /**
     * 所属讨论区ID
     */
    private Long forumId;
    
    /**
     * 讨论区名称
     */
    private String forumName;
    
    /**
     * 主题标题
     */
    private String title;
    
    /**
     * 主题内容
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
     * 最后回复时间
     */
    private LocalDateTime lastReplyTime;
    
    /**
     * 浏览数
     */
    private Integer viewCount;
    
    /**
     * 回复数
     */
    private Integer replyCount;
    
    /**
     * 是否置顶
     */
    private Integer isPinned;
    
    /**
     * 状态（normal正常，hidden隐藏）
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 