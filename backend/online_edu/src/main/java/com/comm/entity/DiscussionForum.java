package com.comm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 讨论区实体类
 */
@Data
@TableName("comm_discussion_forum")
public class DiscussionForum {
    
    /**
     * 讨论区ID
     */
    @TableId(value = "forum_id", type = IdType.AUTO)
    private Long forumId;
    
    /**
     * 关联的课程ID
     */
    @TableField("course_id")
    private Long courseId;
    
    /**
     * 讨论区名称
     */
    @TableField("forum_name")
    private String forumName;
    
    /**
     * 讨论区描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 是否公开（0否，1是）
     */
    @TableField("is_public")
    private Integer isPublic;
    
    /**
     * 状态（1正常，0停用）
     */
    @TableField("status")
    private Integer status;
    
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
     * 备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 课程名称（非数据库字段）
     */
    @TableField(exist = false)
    private String courseName;
} 