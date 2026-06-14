package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.mapper.EduResourceTypeMapper;
import com.course.mapper.EduTeachingResourceMapper;
import com.course.model.entity.EduResourceType;
import com.course.model.entity.EduTeachingResource;
import com.course.service.EduResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源类型服务实现类
 */
@Service
public class EduResourceTypeServiceImpl extends ServiceImpl<EduResourceTypeMapper, EduResourceType> implements EduResourceTypeService {

    @Autowired
    private EduTeachingResourceMapper teachingResourceMapper;

    @Override
    public Page<EduResourceType> listResourceTypes(Page<EduResourceType> page, String typeName) {
        QueryWrapper<EduResourceType> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件（如果有）
        if (StringUtils.hasText(typeName)) {
            queryWrapper.like("type_name", typeName);
        }
        // 按创建时间降序排序
        queryWrapper.orderByDesc("create_time");
        
        return this.page(page, queryWrapper);
    }

    @Override
    public List<EduResourceType> getAllEnabledTypes() {
        LambdaQueryWrapper<EduResourceType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduResourceType::getStatus, 1); // 只获取启用状态的资源类型
        queryWrapper.orderByAsc(EduResourceType::getTypeId);
        return this.list(queryWrapper);
    }

    @Override
    public EduResourceType getResourceTypeById(Long typeId) {
        return this.getById(typeId);
    }

    @Override
    @Transactional
    public boolean createResourceType(EduResourceType resourceType) {
        // 设置创建时间
        resourceType.setCreateTime(LocalDateTime.now());
        
        // 设置默认状态为启用
        if (resourceType.getStatus() == null) {
            resourceType.setStatus(1);
        }
        
        return this.save(resourceType);
    }

    @Override
    @Transactional
    public boolean updateResourceType(EduResourceType resourceType) {
        // 设置更新时间
        resourceType.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(resourceType);
    }

    @Override
    @Transactional
    public boolean deleteResourceType(Long typeId) {
        // 检查该类型是否被教学资源使用
        if (isResourceTypeInUse(typeId)) {
            throw new RuntimeException("该资源类型已被使用，无法删除");
        }
        
        return this.removeById(typeId);
    }

    @Override
    @Transactional
    public boolean updateResourceTypeStatus(Long typeId, Integer status) {
        EduResourceType resourceType = new EduResourceType();
        resourceType.setTypeId(typeId);
        resourceType.setStatus(status);
        resourceType.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(resourceType);
    }

    @Override
    public boolean isResourceTypeInUse(Long typeId) {
        // 查询是否有教学资源使用了该类型
        LambdaQueryWrapper<EduTeachingResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduTeachingResource::getResourceTypeId, typeId);
        queryWrapper.last("LIMIT 1"); // 只需要确认是否有，不需要全部查出来
        
        return teachingResourceMapper.selectCount(queryWrapper) > 0;
    }
} 