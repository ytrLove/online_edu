package com.comm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 讨论回复实体类
 */
@Data
@TableName("comm_discussion_reply")
public class DiscussionReply {
    
    /**
     * 回复ID
     */
    @TableId(value = "reply_id", type = IdType.AUTO)
    private Long replyId;
    
    /**
     * 主题ID
     */
    @TableField("topic_id")
    private Long topicId;
    
    /**
     * 回复内容
     */
    @TableField("content")
    private String content;
    
    /**
     * 创建者ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 父回复ID
     */
    @TableField("parent_id")
    private Long parentId;
    
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
     * 子回复列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<DiscussionReply> childReplies;
} 