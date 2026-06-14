package com.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.model.entity.EduResourceType;

import java.util.List;

/**
 * 资源类型服务接口
 */
public interface EduResourceTypeService extends IService<EduResourceType> {
    
    /**
     * 分页查询资源类型列表
     *
     * @param page 分页参数
     * @param typeName 类型名称（可选，用于模糊查询）
     * @return 分页结果
     */
    Page<EduResourceType> listResourceTypes(Page<EduResourceType> page, String typeName);
    
    /**
     * 获取所有启用的资源类型
     *
     * @return 资源类型列表
     */
    List<EduResourceType> getAllEnabledTypes();
    
    /**
     * 根据ID获取资源类型详情
     *
     * @param typeId 资源类型ID
     * @return 资源类型详情
     */
    EduResourceType getResourceTypeById(Long typeId);
    
    /**
     * 创建资源类型
     *
     * @param resourceType 资源类型信息
     * @return 是否成功
     */
    boolean createResourceType(EduResourceType resourceType);
    
    /**
     * 更新资源类型
     *
     * @param resourceType 资源类型信息
     * @return 是否成功
     */
    boolean updateResourceType(EduResourceType resourceType);
    
    /**
     * 删除资源类型
     *
     * @param typeId 资源类型ID
     * @return 是否成功
     */
    boolean deleteResourceType(Long typeId);
    
    /**
     * 启用/禁用资源类型
     *
     * @param typeId 资源类型ID
     * @param status 状态（1-启用，0-禁用）
     * @return 是否成功
     */
    boolean updateResourceTypeStatus(Long typeId, Integer status);
    
    /**
     * 检查资源类型是否被教学资源使用
     *
     * @param typeId 资源类型ID
     * @return 是否被使用
     */
    boolean isResourceTypeInUse(Long typeId);
} 