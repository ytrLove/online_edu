package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.course.model.entity.EduResourceType;
import com.course.service.EduResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源类型管理控制器
 */
@RestController
@RequestMapping("/api/edu/resource/type")
public class EduResourceTypeController {

    @Autowired
    private EduResourceTypeService resourceTypeService;

    /**
     * 分页查询资源类型列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('edu:resource:type:query')")
    public AjaxResult<IPage<EduResourceType>> listResourceTypes(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "typeName", required = false) String typeName
    ) {
        Page<EduResourceType> page = new Page<>(pageNum, pageSize);
        Page<EduResourceType> result = resourceTypeService.listResourceTypes(page, typeName);
        return AjaxResult.success(result);
    }

    /**
     * 获取所有启用的资源类型
     */
    @GetMapping("/all")
    public AjaxResult<List<EduResourceType>> getAllEnabledTypes() {
        List<EduResourceType> types = resourceTypeService.getAllEnabledTypes();
        return AjaxResult.success(types);
    }

    /**
     * 根据ID获取资源类型详情
     */
    @GetMapping("/{typeId}")
    @PreAuthorize("hasAuthority('edu:resource:type:query')")
    public AjaxResult getResourceTypeById(@PathVariable Long typeId) {
        EduResourceType resourceType = resourceTypeService.getResourceTypeById(typeId);
        if (resourceType != null) {
            return AjaxResult.success(resourceType);
        } else {
            return AjaxResult.fail("资源类型不存在");
        }
    }

    /**
     * 创建资源类型
     */
    @PostMapping
    @PreAuthorize("hasAuthority('edu:resource:type:add')")
    public AjaxResult createResourceType(@RequestBody EduResourceType resourceType) {
        boolean success = resourceTypeService.createResourceType(resourceType);
        if (success) {
            return AjaxResult.success(resourceType);
        } else {
            return AjaxResult.fail("创建失败");
        }
    }

    /**
     * 更新资源类型
     */
    @PutMapping
    @PreAuthorize("hasAuthority('edu:resource:type:edit')")
    public AjaxResult updateResourceType(@RequestBody EduResourceType resourceType) {
        boolean success = resourceTypeService.updateResourceType(resourceType);
        if (success) {
            return AjaxResult.success(true);
        } else {
            return AjaxResult.fail("更新失败");
        }
    }

    /**
     * 删除资源类型
     */
    @DeleteMapping("/{typeId}")
    @PreAuthorize("hasAuthority('edu:resource:type:remove')")
    public AjaxResult deleteResourceType(@PathVariable Long typeId) {
        try {
            boolean success = resourceTypeService.deleteResourceType(typeId);
            if (success) {
                return AjaxResult.success(true);
            } else {
                return AjaxResult.fail("删除失败");
            }
        } catch (RuntimeException e) {
            return AjaxResult.fail(e.getMessage());
        }
    }

    /**
     * 更新资源类型状态
     */
    @PutMapping("/{typeId}/status/{status}")
    @PreAuthorize("hasAuthority('edu:resource:type:edit')")
    public AjaxResult updateResourceTypeStatus(
            @PathVariable Long typeId,
            @PathVariable Integer status
    ) {
        if (status != 0 && status != 1) {
            return AjaxResult.fail("状态值无效");
        }
        
        boolean success = resourceTypeService.updateResourceTypeStatus(typeId, status);
        if (success) {
            return AjaxResult.success(true);
        } else {
            return AjaxResult.fail(status == 1 ? "启用失败" : "禁用失败");
        }
    }

    /**
     * 检查资源类型是否被使用
     */
    @GetMapping("/{typeId}/check")
    @PreAuthorize("hasAuthority('edu:resource:type:query')")
    public AjaxResult<Boolean> checkResourceTypeInUse(@PathVariable Long typeId) {
        boolean inUse = resourceTypeService.isResourceTypeInUse(typeId);
        return AjaxResult.success(inUse);
    }
} 