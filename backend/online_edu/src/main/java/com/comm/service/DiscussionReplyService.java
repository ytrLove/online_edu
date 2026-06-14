package com.comm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.dto.ReplyDTO;
import com.comm.dto.request.ReplyCreateRequest;
import com.comm.dto.request.ReplyQueryRequest;
import com.comm.dto.request.ReplyUpdateRequest;
import com.comm.entity.DiscussionReply;

/**
 * 讨论回复服务接口
 */
public interface DiscussionReplyService {
    
    /**
     * 分页查询回复列表
     *
     * @param request 查询参数
     * @return 回复分页列表
     */
    Page<ReplyDTO> queryReplyList(ReplyQueryRequest request);
    
    /**
     * 创建回复
     *
     * @param request 创建参数
     * @param userId 当前用户ID
     * @return 创建后的回复信息
     */
    ReplyDTO createReply(ReplyCreateRequest request, Long userId);
    
    /**
     * 更新回复
     *
     * @param request 更新参数
     * @param userId 当前用户ID
     * @return 是否更新成功
     */
    boolean updateReply(ReplyUpdateRequest request, Long userId);
    
    /**
     * 删除回复
     *
     * @param replyId 回复ID
     * @param userId 当前用户ID
     * @return 是否删除成功
     */
    boolean deleteReply(Long replyId, Long userId);
    
    /**
     * 检查回复是否存在
     *
     * @param replyId 回复ID
     * @return 回复实体
     */
    DiscussionReply checkReplyExists(Long replyId);
} 