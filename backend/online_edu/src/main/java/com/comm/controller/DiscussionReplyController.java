package com.comm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.dto.ReplyDTO;
import com.comm.dto.request.ReplyCreateRequest;
import com.comm.dto.request.ReplyQueryRequest;
import com.comm.dto.request.ReplyUpdateRequest;
import com.comm.service.DiscussionReplyService;
import com.common.core.domain.AjaxResult;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 讨论回复控制器
 */
@RestController
@RequestMapping("/api/discussion/reply")
public class DiscussionReplyController {
    
    @Autowired
    private DiscussionReplyService replyService;
    
    /**
     * 获取主题下的回复列表
     */
    @GetMapping("/list/{topicId}")
    @PreAuthorize("permitAll()")
    public AjaxResult list(@PathVariable Long topicId, ReplyQueryRequest request) {
        request.setTopicId(topicId);
        Page<ReplyDTO> page = replyService.queryReplyList(request);
        return AjaxResult.success(page);
    }
    
    /**
     * 创建回复
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public AjaxResult create(@RequestBody ReplyCreateRequest request) {
        // 获取当前登录用户ID
        Long userId = getCurrentUserId();
        
        ReplyDTO reply = replyService.createReply(request, userId);
        return AjaxResult.success(reply);
    }
    
    /**
     * 更新回复
     */
    @PutMapping
    @PreAuthorize("hasAuthority('comm:reply:edit')")
    public AjaxResult update(@RequestBody ReplyUpdateRequest request) {
        // 获取当前登录用户ID
        Long userId = getCurrentUserId();
        
        boolean result = replyService.updateReply(request, userId);
        return AjaxResult.success(result);
    }
    
    /**
     * 删除回复
     */
    @DeleteMapping("/{replyId}")
    @PreAuthorize("hasAuthority('comm:reply:remove')")
    public AjaxResult delete(@PathVariable Long replyId) {
        // 获取当前登录用户ID
        Long userId = getCurrentUserId();
        
        boolean result = replyService.deleteReply(replyId, userId);
        return AjaxResult.success(result);
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // TODO: 根据项目中的认证方式来获取用户ID
        // 暂时模拟返回一个用户ID
        SysUser u = (SysUser) authentication.getPrincipal();
        if (u != null) {
            return u.getUserId();
        }
        return 1L;
    }
} 