package com.comm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comm.dto.ForumDTO;
import com.comm.dto.request.ForumCreateRequest;
import com.comm.dto.request.ForumQueryRequest;
import com.comm.dto.request.ForumUpdateRequest;
import com.comm.entity.DiscussionForum;
import com.comm.mapper.DiscussionForumMapper;
import com.comm.service.DiscussionForumService;
import com.util.CheckRole;
import com.util.GetId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 讨论区服务实现类
 */
@Service
public class DiscussionForumServiceImpl extends ServiceImpl<DiscussionForumMapper, DiscussionForum> implements DiscussionForumService {

    @Autowired
    private GetId getId;

    @Autowired
    private CheckRole checkRole;

    @Override
    public Page<ForumDTO> queryForumList(ForumQueryRequest request) {
        Page<DiscussionForum> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<DiscussionForum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiscussionForum::getDelFlag, 0); // 未删除的记录
        
        // 根据角色设置不同的查询条件
        if (checkRole.isTeacher()) {
            // 教师只能看到自己所教授课程的讨论区
            Long teacherId = getId.getId();
            wrapper.exists("SELECT 1 FROM edu_course_teacher ct WHERE ct.course_id = comm_discussion_forum.course_id AND ct.teacher_id = " + teacherId);
        } else if (checkRole.isStudent()) {
            // 学生只能看到自己所在课程的讨论区
            Long studentId = getId.getId();
            wrapper.exists("SELECT 1 FROM edu_course_student sc WHERE sc.student_id = " + studentId +
                          " AND sc.course_id = comm_discussion_forum.course_id");
        } else if (!checkRole.isAdmin()) {
            // 非管理员、非教师、非学生的用户不能查看任何讨论区
            return new Page<>();
        }
        
        // 添加其他查询条件
        if (request.getCourseId() != null) {
            wrapper.eq(DiscussionForum::getCourseId, request.getCourseId());
        }
        if (StringUtils.hasText(request.getForumName())) {
            wrapper.like(DiscussionForum::getForumName, request.getForumName());
        }
        if (request.getIsPublic() != null) {
            wrapper.eq(DiscussionForum::getIsPublic, request.getIsPublic());
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(DiscussionForum::getCreateTime);
        
        // 执行查询
        Page<DiscussionForum> forumPage = baseMapper.selectPage(page, wrapper);
        
        // 转换为DTO
        List<ForumDTO> forumDTOList = convertToDTO(forumPage.getRecords());
        
        // 构建返回结果
        Page<ForumDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(forumPage, resultPage, "records");
        resultPage.setRecords(forumDTOList);
        
        return resultPage;
    }

    @Override
    public ForumDTO getForumById(Long forumId) {
        DiscussionForum forum = getById(forumId);
        if (forum == null || forum.getDelFlag() == 1) {
            throw new RuntimeException("讨论区不存在");
        }
        
        return convertToDTO(forum);
    }

    @Override
    @Transactional
    public ForumDTO createForum(ForumCreateRequest request) {
        DiscussionForum forum = new DiscussionForum();
        BeanUtils.copyProperties(request, forum);
        
        // 设置默认值
        forum.setDelFlag(0);
        forum.setCreateTime(LocalDateTime.now());
        forum.setUpdateTime(LocalDateTime.now());
        
        // 保存
        save(forum);
        
        return convertToDTO(forum);
    }

    @Override
    @Transactional
    public boolean updateForum(ForumUpdateRequest request) {
        Long forumId = request.getForumId();
        DiscussionForum forum = checkForumExists(forumId);
        
        BeanUtils.copyProperties(request, forum);
        forum.setUpdateTime(LocalDateTime.now());
        
        return updateById(forum);
    }

    @Override
    @Transactional
    public boolean deleteForum(Long forumId) {
        DiscussionForum forum = checkForumExists(forumId);
        
        // 逻辑删除
        forum.setDelFlag(1);
        forum.setUpdateTime(LocalDateTime.now());
        
        return updateById(forum);
    }

    @Override
    public DiscussionForum checkForumExists(Long forumId) {
        DiscussionForum forum = getById(forumId);
        if (forum == null || forum.getDelFlag() == 1) {
            throw new RuntimeException("讨论区不存在");
        }
        
        return forum;
    }
    
    /**
     * 将实体转换为DTO
     */
    private ForumDTO convertToDTO(DiscussionForum forum) {
        if (forum == null) {
            return null;
        }
        
        ForumDTO dto = new ForumDTO();
        BeanUtils.copyProperties(forum, dto);
        
        return dto;
    }
    
    /**
     * 将实体列表转换为DTO列表
     */
    private List<ForumDTO> convertToDTO(List<DiscussionForum> forumList) {
        if (forumList == null || forumList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return forumList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 