package com.comm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.dto.TopicDTO;
import com.comm.dto.request.TopicCreateRequest;
import com.comm.dto.request.TopicQueryRequest;
import com.comm.dto.request.TopicUpdateRequest;
import com.comm.service.DiscussionTopicService;
import com.common.core.domain.AjaxResult;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 讨论主题控制器
 */
@RestController
@RequestMapping("/api/discussion/topic")
public class DiscussionTopicController {

    
    @Autowired
    private DiscussionTopicService topicService;
    
    /**
     * 获取主题列表
     */
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public AjaxResult list(TopicQueryRequest request) {
        Page<TopicDTO> page = topicService.queryTopicList(request);
        return AjaxResult.success(page);
    }
    
    /**
     * 获取主题详情
     */
    @GetMapping("/{topicId}")
    @PreAuthorize("permitAll()")
    public AjaxResult getInfo(@PathVariable Long topicId) {
        TopicDTO topic = topicService.getTopicById(topicId);
        return AjaxResult.success(topic);
    }
    
    /**
     * 创建主题
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public AjaxResult create(@RequestBody TopicCreateRequest request) {
        // 获取当前登录用户ID
        Long userId = getCurrentUserId();

        
        TopicDTO topic = topicService.createTopic(request, userId);
        return AjaxResult.success(topic);
    }
    
    /**
     * 更新主题
     */
    @PutMapping
    @PreAuthorize("hasAuthority('comm:topic:edit')")
    public AjaxResult update(@RequestBody TopicUpdateRequest request) {
        // 获取当前登录用户ID
        Long userId = getCurrentUserId();
        
        boolean result = topicService.updateTopic(request, userId);
        return AjaxResult.success(result);
    }
    
    /**
     * 删除主题
     */
    @DeleteMapping("/{topicId}")
    @PreAuthorize("hasAuthority('comm:topic:remove')")
    public AjaxResult delete(@PathVariable Long topicId) {
        // 获取当前登录用户ID
        Long userId = getCurrentUserId();
        
        boolean result = topicService.deleteTopic(topicId, userId);
        return AjaxResult.success(result);
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SysUser u = (SysUser) authentication.getPrincipal();
        if (u != null) {
            return u.getUserId();
        }
        return 1L;
    }
} 