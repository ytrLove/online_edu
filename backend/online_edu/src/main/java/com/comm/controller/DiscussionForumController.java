package com.comm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.dto.ForumDTO;
import com.comm.dto.request.ForumCreateRequest;
import com.comm.dto.request.ForumQueryRequest;
import com.comm.dto.request.ForumUpdateRequest;
import com.comm.service.DiscussionForumService;
import com.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 讨论区控制器
 */
@RestController
@RequestMapping("/api/discussion/forum")
public class DiscussionForumController {
    
    @Autowired
    private DiscussionForumService forumService;
    
    /**
     * 获取讨论区列表
     */
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public AjaxResult list(ForumQueryRequest request) {
        Page<ForumDTO> page = forumService.queryForumList(request);
        return AjaxResult.success(page);
    }
    
    /**
     * 获取讨论区详情
     */
    @GetMapping("/{forumId}")
    @PreAuthorize("permitAll()")
    public AjaxResult getInfo(@PathVariable Long forumId) {
        ForumDTO forum = forumService.getForumById(forumId);
        return AjaxResult.success(forum);
    }
    
    /**
     * 创建讨论区
     */
    @PostMapping
    @PreAuthorize("hasAuthority('comm:forum:add')")
    public AjaxResult create(@RequestBody ForumCreateRequest request) {
        ForumDTO forum = forumService.createForum(request);
        return AjaxResult.success(forum);
    }
    
    /**
     * 更新讨论区
     */
    @PutMapping
    @PreAuthorize("hasAuthority('comm:forum:edit')")
    public AjaxResult update(@RequestBody ForumUpdateRequest request) {
        boolean result = forumService.updateForum(request);
        return AjaxResult.success(result);
    }
    
    /**
     * 删除讨论区
     */
    @DeleteMapping("/{forumId}")
    @PreAuthorize("hasAuthority('comm:forum:remove')")
    public AjaxResult delete(@PathVariable Long forumId) {
        boolean result = forumService.deleteForum(forumId);
        return AjaxResult.success(result);
    }
} 