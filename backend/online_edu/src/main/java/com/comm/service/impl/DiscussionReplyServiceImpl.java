package com.comm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comm.dto.ReplyDTO;
import com.comm.dto.request.ReplyCreateRequest;
import com.comm.dto.request.ReplyQueryRequest;
import com.comm.dto.request.ReplyUpdateRequest;
import com.comm.entity.DiscussionReply;
import com.comm.entity.DiscussionTopic;
import com.comm.mapper.DiscussionReplyMapper;
import com.comm.service.DiscussionReplyService;
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
 * 讨论回复服务实现类
 */
@Service
public class DiscussionReplyServiceImpl extends ServiceImpl<DiscussionReplyMapper, DiscussionReply> implements DiscussionReplyService {

    @Autowired
    private DiscussionTopicService topicService;

    @Override
    public Page<ReplyDTO> queryReplyList(ReplyQueryRequest request) {
        // 检查主题是否存在
        topicService.checkTopicExists(request.getTopicId());
        
        Page<DiscussionReply> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        // 查询顶层回复
        Page<DiscussionReply> replyPage = baseMapper.selectReplyList(page, 
                request.getTopicId(), 
                request.getParentId());
        
        // 如果是顶层回复，需要查询子回复
        List<DiscussionReply> replyList = replyPage.getRecords();
        if (request.getParentId() == null) {
            for (DiscussionReply reply : replyList) {
                List<DiscussionReply> childReplies = baseMapper.selectChildReplies(reply.getReplyId());
                reply.setChildReplies(childReplies);
            }
        }
        
        // 转换为DTO
        List<ReplyDTO> replyDTOList = convertToDTO(replyList);
        
        Page<ReplyDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(replyPage, resultPage, "records");
        resultPage.setRecords(replyDTOList);
        
        return resultPage;
    }

    @Override
    @Transactional
    public ReplyDTO createReply(ReplyCreateRequest request, Long userId) {
        // 检查主题是否存在
        DiscussionTopic topic = topicService.checkTopicExists(request.getTopicId());
        
        // 如果是回复其他回复，检查父回复是否存在
        if (request.getParentId() != null) {
            checkReplyExists(request.getParentId());
        }
        
        DiscussionReply reply = new DiscussionReply();
        BeanUtils.copyProperties(request, reply);
        
        // 设置其他字段
        reply.setUserId(userId);
        reply.setDelFlag(0);
        reply.setCreateTime(LocalDateTime.now());
        reply.setUpdateTime(LocalDateTime.now());
        
        // 保存
        save(reply);
        
        // 更新主题回复数和最后回复时间
        topicService.updateReplyCountAndTime(request.getTopicId(), 1);
        
        return convertToDTO(reply);
    }

    @Override
    @Transactional
    public boolean updateReply(ReplyUpdateRequest request, Long userId) {
        Long replyId = request.getReplyId();
        DiscussionReply reply = checkReplyExists(replyId);
        
        // 检查是否是回复创建者
        checkReplyOwner(reply, userId);
        
        // 更新回复
        reply.setContent(request.getContent());
        reply.setUpdateTime(LocalDateTime.now());
        
        return updateById(reply);
    }

    @Override
    @Transactional
    public boolean deleteReply(Long replyId, Long userId) {
        DiscussionReply reply = checkReplyExists(replyId);
        
        // 检查是否是回复创建者
        checkReplyOwner(reply, userId);
        
        // 获取主题ID
        Long topicId = reply.getTopicId();
        
        // 逻辑删除
        reply.setDelFlag(1);
        reply.setUpdateTime(LocalDateTime.now());
        boolean result = updateById(reply);
        
        // 更新主题回复数和最后回复时间
        if (result) {
            topicService.updateReplyCountAndTime(topicId, -1);
        }
        
        return result;
    }

    @Override
    public DiscussionReply checkReplyExists(Long replyId) {
        DiscussionReply reply = getById(replyId);
        if (reply == null || reply.getDelFlag() == 1) {
            throw new RuntimeException("回复不存在");
        }
        
        return reply;
    }
    
    /**
     * 检查用户是否是回复创建者
     */
    private void checkReplyOwner(DiscussionReply reply, Long userId) {
        // TODO: 添加管理员权限检查
        if (!reply.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此回复");
        }
    }
    
    /**
     * 将实体转换为DTO
     */
    private ReplyDTO convertToDTO(DiscussionReply reply) {
        if (reply == null) {
            return null;
        }
        
        ReplyDTO dto = new ReplyDTO();
        BeanUtils.copyProperties(reply, dto);
        
        // 处理子回复
        if (reply.getChildReplies() != null && !reply.getChildReplies().isEmpty()) {
            dto.setChildReplies(convertToDTO(reply.getChildReplies()));
        }
        
        return dto;
    }
    
    /**
     * 将实体列表转换为DTO列表
     */
    private List<ReplyDTO> convertToDTO(List<DiscussionReply> replyList) {
        if (replyList == null || replyList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return replyList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 