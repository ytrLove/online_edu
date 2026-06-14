package com.comm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.entity.DiscussionForum;
import org.apache.ibatis.annotations.Param;

/**
 * 讨论区数据访问接口
 */
public interface DiscussionForumMapper extends BaseMapper<DiscussionForum> {
    
    /**
     * 分页查询讨论区列表
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @param forumName 讨论区名称
     * @param isPublic 是否公开
     * @return 讨论区分页列表
     */
    Page<DiscussionForum> selectForumList(
            @Param("page") Page<DiscussionForum> page,
            @Param("courseId") Long courseId,
            @Param("forumName") String forumName,
            @Param("isPublic") Integer isPublic);
} 