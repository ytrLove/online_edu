package com.comm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comm.dto.TopicDTO;
import com.comm.dto.request.TopicCreateRequest;
import com.comm.dto.request.TopicQueryRequest;
import com.comm.dto.request.TopicUpdateRequest;
import com.comm.entity.DiscussionForum;
import com.comm.entity.DiscussionTopic;
import com.comm.mapper.DiscussionTopicMapper;
import com.comm.service.DiscussionForumService;
import com.comm.service.DiscussionTopicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 讨论主题服务实现类
 */
@Service
public class DiscussionTopicServiceImpl extends ServiceImpl<DiscussionTopicMapper, DiscussionTopic> implements DiscussionTopicService {

    @Autowired
    private DiscussionForumService forumService;

    @Override
    public Page<TopicDTO> queryTopicList(TopicQueryRequest request) {
        Page<DiscussionTopic> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        Page<DiscussionTopic> topicPage = baseMapper.selectTopicList(page, 
                request.getForumId(), 
                request.getTitle(), 
                request.getUserId(), 
                request.getIsPinned());
        
        // 转换为DTO
        List<TopicDTO> topicDTOList = convertToDTO(topicPage.getRecords());
        
        Page<TopicDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(topicPage, resultPage, "records");
        resultPage.setRecords(topicDTOList);
        
        return resultPage;
    }

    @Override
    public TopicDTO getTopicById(Long topicId) {
        // 查询主题详情
        DiscussionTopic topic = baseMapper.selectTopicDetail(topicId);
        if (topic == null || topic.getDelFlag() == 1) {
            throw new RuntimeException("主题不存在");
        }
        
        // 增加浏览数
        baseMapper.incrementViewCount(topicId);
        
        return convertToDTO(topic);
    }

    @Override
    @Transactional
    public TopicDTO createTopic(TopicCreateRequest request, Long userId) {
        // 检查讨论区是否存在
        DiscussionForum forum = forumService.checkForumExists(request.getForumId());
        
        DiscussionTopic topic = new DiscussionTopic();
        BeanUtils.copyProperties(request, topic);
        
        // 设置其他字段
        topic.setUserId(userId);
        topic.setLastReplyTime(LocalDateTime.now());
        topic.setViewCount(0);
        topic.setReplyCount(0);
        topic.setIsPinned(0);
        topic.setStatus("normal");
        topic.setDelFlag(0);
        topic.setCreateTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());
        
        // 保存
        save(topic);
        
        return convertToDTO(topic);
    }

    @Override
    @Transactional
    public boolean updateTopic(TopicUpdateRequest request, Long userId) {
        Long topicId = request.getTopicId();
        DiscussionTopic topic = checkTopicExists(topicId);
        
        // 检查是否是主题创建者（或管理员）
        checkTopicOwner(topic, userId);
        
        // 更新主题
        if (request.getTitle() != null) {
            topic.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            topic.setContent(request.getContent());
        }
        if (request.getIsPinned() != null) {
            topic.setIsPinned(request.getIsPinned());
        }
        
        topic.setUpdateTime(LocalDateTime.now());
        
        return updateById(topic);
    }

    @Override
    @Transactional
    public boolean deleteTopic(Long topicId, Long userId) {
        DiscussionTopic topic = checkTopicExists(topicId);
        
        // 检查是否是主题创建者（或管理员）
        checkTopicOwner(topic, userId);
        
        // 逻辑删除
        topic.setDelFlag(1);
        topic.setUpdateTime(LocalDateTime.now());
        
        return updateById(topic);
    }

    @Override
    public DiscussionTopic checkTopicExists(Long topicId) {
        DiscussionTopic topic = getById(topicId);
        if (topic == null || topic.getDelFlag() == 1) {
            throw new RuntimeException("主题不存在");
        }
        
        return topic;
    }

    @Override
    @Transactional
    public boolean updateReplyCountAndTime(Long topicId, int increment) {
        return baseMapper.updateReplyCountAndTime(topicId, increment) > 0;
    }
    
    /**
     * 检查用户是否是主题创建者
     */
    private void checkTopicOwner(DiscussionTopic topic, Long userId) {
        // TODO: 添加管理员权限检查
        if (!topic.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此主题");
        }
    }
    
    /**
     * 将实体转换为DTO
     */
    private TopicDTO convertToDTO(DiscussionTopic topic) {
        if (topic == null) {
            return null;
        }
        
        TopicDTO dto = new TopicDTO();
        BeanUtils.copyProperties(topic, dto);
        
        return dto;
    }
    
    /**
     * 将实体列表转换为DTO列表
     */
    private List<TopicDTO> convertToDTO(List<DiscussionTopic> topicList) {
        if (topicList == null || topicList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return topicList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 