package com.comm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.dto.TopicDTO;
import com.comm.dto.request.TopicCreateRequest;
import com.comm.dto.request.TopicQueryRequest;
import com.comm.dto.request.TopicUpdateRequest;
import com.comm.entity.DiscussionTopic;

/**
 * 讨论主题服务接口
 */
public interface DiscussionTopicService {
    
    /**
     * 分页查询主题列表
     *
     * @param request 查询参数
     * @return 主题分页列表
     */
    Page<TopicDTO> queryTopicList(TopicQueryRequest request);
    
    /**
     * 根据ID获取主题详情
     *
     * @param topicId 主题ID
     * @return 主题详情
     */
    TopicDTO getTopicById(Long topicId);
    
    /**
     * 创建主题
     *
     * @param request 创建参数
     * @param userId 当前用户ID
     * @return 创建后的主题信息
     */
    TopicDTO createTopic(TopicCreateRequest request, Long userId);
    
    /**
     * 更新主题
     *
     * @param request 更新参数
     * @param userId 当前用户ID
     * @return 是否更新成功
     */
    boolean updateTopic(TopicUpdateRequest request, Long userId);
    
    /**
     * 删除主题
     *
     * @param topicId 主题ID
     * @param userId 当前用户ID
     * @return 是否删除成功
     */
    boolean deleteTopic(Long topicId, Long userId);
    
    /**
     * 检查主题是否存在
     *
     * @param topicId 主题ID
     * @return 主题实体
     */
    DiscussionTopic checkTopicExists(Long topicId);
    
    /**
     * 更新主题回复数和最后回复时间
     *
     * @param topicId 主题ID
     * @param increment 增加的回复数
     * @return 是否更新成功
     */
    boolean updateReplyCountAndTime(Long topicId, int increment);
} 