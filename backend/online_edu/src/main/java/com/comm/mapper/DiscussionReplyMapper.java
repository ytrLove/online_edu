package com.comm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.entity.DiscussionReply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 讨论回复数据访问接口
 */
public interface DiscussionReplyMapper extends BaseMapper<DiscussionReply> {
    
    /**
     * 分页查询主题下的回复列表
     *
     * @param page 分页参数
     * @param topicId 主题ID
     * @param parentId 父回复ID
     * @return 回复分页列表
     */
    Page<DiscussionReply> selectReplyList(
            @Param("page") Page<DiscussionReply> page,
            @Param("topicId") Long topicId,
            @Param("parentId") Long parentId);
    
    /**
     * 查询回复的子回复列表
     *
     * @param parentId 父回复ID
     * @return 子回复列表
     */
    List<DiscussionReply> selectChildReplies(@Param("parentId") Long parentId);
} 