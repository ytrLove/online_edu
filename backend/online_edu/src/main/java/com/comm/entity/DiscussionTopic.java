package com.comm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 讨论主题实体类
 */
@Data
@TableName("comm_discussion_topic")
public class DiscussionTopic {
    
    /**
     * 主题ID
     */
    @TableId(value = "topic_id", type = IdType.AUTO)
    private Long topicId;
    
    /**
     * 所属讨论区ID
     */
    @TableField("forum_id")
    private Long forumId;
    
    /**
     * 主题标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 主题内容
     */
    @TableField("content")
    private String content;
    
    /**
     * 创建者ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 最后回复时间
     */
    @TableField("last_reply_time")
    private LocalDateTime lastReplyTime;
    
    /**
     * 浏览数
     */
    @TableField("view_count")
    private Integer viewCount;
    
    /**
     * 回复数
     */
    @TableField("reply_count")
    private Integer replyCount;
    
    /**
     * 是否置顶
     */
    @TableField("is_pinned")
    private Integer isPinned;
    
    /**
     * 状态（normal正常，hidden隐藏）
     */
    @TableField("status")
    private String status;
    
    /**
     * 删除标志
     */
    @TableField("del_flag")
    private Integer delFlag;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    /**
     * 创建者用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String username;
    
    /**
     * 创建者头像（非数据库字段）
     */
    @TableField(exist = false)
    private String avatar;
    
    /**
     * 讨论区名称（非数据库字段）
     */
    @TableField(exist = false)
    private String forumName;
} 