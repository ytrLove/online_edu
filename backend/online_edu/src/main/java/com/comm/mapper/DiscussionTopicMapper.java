package com.comm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.entity.DiscussionTopic;
import org.apache.ibatis.annotations.Param;

/**
 * 讨论主题数据访问接口
 */
public interface DiscussionTopicMapper extends BaseMapper<DiscussionTopic> {
    
    /**
     * 分页查询主题列表
     *
     * @param page 分页参数
     * @param forumId 讨论区ID
     * @param title 主题标题
     * @param userId 创建者ID
     * @param isPinned 是否置顶
     * @return 主题分页列表
     */
    Page<DiscussionTopic> selectTopicList(
            @Param("page") Page<DiscussionTopic> page,
            @Param("forumId") Long forumId,
            @Param("title") String title,
            @Param("userId") Long userId,
            @Param("isPinned") Integer isPinned);
    
    /**
     * 查询主题详情
     *
     * @param topicId 主题ID
     * @return 主题详情
     */
    DiscussionTopic selectTopicDetail(@Param("topicId") Long topicId);
    
    /**
     * 增加主题浏览数
     *
     * @param topicId 主题ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("topicId") Long topicId);
    
    /**
     * 更新主题回复数和最后回复时间
     *
     * @param topicId 主题ID
     * @param increment 增加的回复数
     * @return 影响行数
     */
    int updateReplyCountAndTime(@Param("topicId") Long topicId, @Param("increment") int increment);
} 