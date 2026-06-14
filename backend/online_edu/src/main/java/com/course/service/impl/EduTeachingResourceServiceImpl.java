package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.config.MinioConfig;
import com.course.mapper.EduTeachingResourceMapper;
import com.course.model.entity.EduTeacher;
import com.course.model.entity.EduTeachingResource;
import com.course.service.EduResourceTypeService;
import com.course.service.EduTeacherService;
import com.course.service.EduTeachingResourceService;
import com.course.service.MinIOService;
import com.user.model.entity.SysRole;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EduTeachingResourceServiceImpl extends ServiceImpl<EduTeachingResourceMapper, EduTeachingResource> implements EduTeachingResourceService {
    @Autowired
    private EduResourceTypeService eduResourceTypeService;
    
    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private MinIOService minIOService;

    @Autowired
    private MinioConfig minioConfig;
    
    @Override
    public IPage<EduTeachingResource> listResources(EduTeachingResource resource, Integer pageNum, Integer pageSize) {
        Page<EduTeachingResource> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectResourceList(page, resource);
    }

    @Override
    public EduTeachingResource getResourceById(Long resourceId) {
        return getById(resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean uploadResource( EduTeachingResource resource) {
        // 判断资源类型是否被禁止
        if (eduResourceTypeService.getResourceTypeById(resource.getResourceTypeId()).getStatus() == 0){
            return false;
        }
        // 判断是否为管理员创建的
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleKey().equals("admin"));
        
        if (isAdmin) {
            resource.setTeacherId(null);
        }
        
        // 设置其他字段
        resource.setStatus(1);
        resource.setDelFlag(0);
        resource.setDownloadCount(0);
        resource.setCreateTime(LocalDateTime.now());
        resource.setUpdateTime(LocalDateTime.now());
        
        // 保存资源信息
        save(resource);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduTeachingResource updateResource(EduTeachingResource resource) {
        resource.setUpdateTime(LocalDateTime.now());
        updateById(resource);
        return resource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteResource(Long resourceId) {
        // 先检查资源是否存在
        EduTeachingResource resource = getById(resourceId);
        if (resource == null) {
            return false;
        }
        String url = resource.getFileUrl();
        String objName = url.substring(url.lastIndexOf(minioConfig.getBucketName() + "/") + minioConfig.getBucketName().length() + 1);

//        System.out.println(fileName);
        // 删除在minio中存储的文件
        minIOService.remove(objName);

        // 使用自定义的逻辑删除方法
        return baseMapper.logicDeleteResource(resourceId) > 0;
    }

    @Override
    public List<EduTeachingResource> getResourcesByCourseId(Long courseId) {
        return list(new LambdaQueryWrapper<EduTeachingResource>()
                .eq(EduTeachingResource::getCourseId, courseId)
                .eq(EduTeachingResource::getDelFlag, 0)
                .orderByDesc(EduTeachingResource::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementDownloadCount(Long resourceId) {
        return baseMapper.incrementDownloadCount(resourceId) > 0;
    }
    
    @Override
    public IPage<EduTeachingResource> getMyResources(EduTeachingResource resource, Integer pageNum, Integer pageSize) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();
        Long userId = user.getUserId();
        
        // 查询用户对应的教师ID
        EduTeacher teacher = teacherService.getTeacherByUserId(userId);
        
        // 创建分页对象
        Page<EduTeachingResource> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EduTeachingResource> queryWrapper = new LambdaQueryWrapper<>();

        boolean isAdmin;
        isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleKey().equals("admin"));
        
        // 添加删除标记条件
        queryWrapper.eq(EduTeachingResource::getDelFlag, 0);
        
        // 添加模糊查询条件 - 资源名称
        if (resource != null && resource.getResourceName() != null && !resource.getResourceName().trim().isEmpty()) {
            queryWrapper.like(EduTeachingResource::getResourceName, resource.getResourceName());
        }
        
        // 添加精确查询条件 - 资源类型ID
        if (resource != null && resource.getResourceTypeId() != null) {
            queryWrapper.eq(EduTeachingResource::getResourceTypeId, resource.getResourceTypeId());
        }

        if (teacher == null && !isAdmin) {
            // 不是教师，并且不是管理员，返回空页
            queryWrapper.eq(EduTeachingResource::getResourceId, -1);
        } else if (isAdmin) {
            // 如果是管理员，返回管理员创建的资源和所有资源
            queryWrapper.isNull(EduTeachingResource::getTeacherId).or().eq(EduTeachingResource::getTeacherId, "");
        } else {
            // 查询该教师的资源
            queryWrapper.eq(EduTeachingResource::getTeacherId, teacher.getTeacherId());
        }
        
        // 添加排序条件
        queryWrapper.orderByDesc(EduTeachingResource::getCreateTime);

        return baseMapper.selectResourceList(page, resource);
    }
    
    @Override
    public IPage<EduTeachingResource> getRoleResources(Integer pageNum, Integer pageSize) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();
        
        // 获取用户角色
        Set<String> roleKeys = user.getRoles().stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
        
        Page<EduTeachingResource> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EduTeachingResource> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据角色设置不同的查询条件
        if (roleKeys.contains("admin")) {
            // 管理员角色可以查看所有资源
            queryWrapper.eq(EduTeachingResource::getDelFlag, 0);
        } else if (roleKeys.contains("teacher")) {
            // 教师角色可以查看公开资源和自己的资源
            Long userId = user.getUserId();
            EduTeacher teacher = teacherService.getTeacherByUserId(userId);
            
            if (teacher != null) {
                queryWrapper.and(wrapper -> wrapper
                        .eq(EduTeachingResource::getIsPublic, 1)
                        .or()
                        .eq(EduTeachingResource::getTeacherId, teacher.getTeacherId())
                );
            } else {
                // 仅查看公开资源
                queryWrapper.eq(EduTeachingResource::getIsPublic, 1);
            }
            queryWrapper.eq(EduTeachingResource::getDelFlag, 0);
        } else if (roleKeys.contains("student")) {
            // 学生角色只能查看公开资源
            queryWrapper.eq(EduTeachingResource::getIsPublic, 1)
                    .eq(EduTeachingResource::getDelFlag, 0);
        }
        
        queryWrapper.orderByDesc(EduTeachingResource::getCreateTime);
        return page(page, queryWrapper);
    }
} 