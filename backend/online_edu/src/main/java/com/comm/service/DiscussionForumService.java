package com.comm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.comm.dto.ForumDTO;
import com.comm.dto.request.ForumCreateRequest;
import com.comm.dto.request.ForumQueryRequest;
import com.comm.dto.request.ForumUpdateRequest;
import com.comm.entity.DiscussionForum;

/**
 * 讨论区服务接口
 */
public interface DiscussionForumService {
    
    /**
     * 分页查询讨论区列表
     *
     * @param request 查询参数
     * @return 讨论区分页列表
     */
    Page<ForumDTO> queryForumList(ForumQueryRequest request);
    
    /**
     * 根据ID获取讨论区详情
     *
     * @param forumId 讨论区ID
     * @return 讨论区详情
     */
    ForumDTO getForumById(Long forumId);
    
    /**
     * 创建讨论区
     *
     * @param request 创建参数
     * @return 创建后的讨论区信息
     */
    ForumDTO createForum(ForumCreateRequest request);
    
    /**
     * 更新讨论区
     *
     * @param request 更新参数
     * @return 是否更新成功
     */
    boolean updateForum(ForumUpdateRequest request);
    
    /**
     * 删除讨论区
     *
     * @param forumId 讨论区ID
     * @return 是否删除成功
     */
    boolean deleteForum(Long forumId);
    
    /**
     * 检查讨论区是否存在
     *
     * @param forumId 讨论区ID
     * @return 讨论区实体
     */
    DiscussionForum checkForumExists(Long forumId);
} 