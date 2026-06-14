package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.course.model.entity.EduTeacher;
import com.course.model.entity.EduTeachingResource;
import com.course.service.EduTeacherService;
import com.course.service.EduTeachingResourceService;
import com.user.model.entity.SysRole;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学资源管理控制器
 */
@RestController
@RequestMapping("/api/edu/resource")
public class EduTeachingResourceController {

    @Autowired
    private EduTeachingResourceService resourceService;
    @Autowired
    private EduTeacherService teacherService;

    /**
     * 分页查询资源列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('edu:resource:list')")
    public AjaxResult<IPage<EduTeachingResource>> list(EduTeachingResource resource,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<EduTeachingResource> page = resourceService.listResources(resource, pageNum, pageSize);
        return AjaxResult.success(page);
    }

    /**
     * 获取资源详情
     */
    @GetMapping("/{resourceId}")
    @PreAuthorize("hasAuthority('edu:resource:query')")
    public AjaxResult<EduTeachingResource> getInfo(@PathVariable Long resourceId) {
        EduTeachingResource resource = resourceService.getResourceById(resourceId);
        return AjaxResult.success(resource);
    }

    /**
     * 上传资源
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('edu:resource:add')")
    public AjaxResult upload(@RequestBody EduTeachingResource resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();
        Long userId = user.getUserId();
        // 通过用户id获取教师对象
        EduTeacher teacher = teacherService.getTeacherByUserId(userId);
        // 判断教师是否存在, 并且不是管理员
        List<SysRole> roles = user.getRoles();
        boolean isAdmin = false;
        if (roles != null) {
            for (SysRole role : roles) {
                if ("admin".equals(role.getRoleKey())) {
                    isAdmin = true;
                    break;
                }
            }
        }

        if (teacher == null && !isAdmin) {
            return AjaxResult.fail("上传失败，您不是教师也不是管理员，无法上传资源");
        }
        if (isAdmin){
            resource.setTeacherId(null);
        }else{
            resource.setTeacherId(teacher.getTeacherId());
        }

        boolean result = resourceService.uploadResource(resource);
        if (result){
            return AjaxResult.success("上传成功");
        }
        return AjaxResult.fail("上传失败");
    }

    /**
     * 更新资源信息
     */
    @PutMapping
    @PreAuthorize("hasAuthority('edu:resource:edit')")
    public AjaxResult<EduTeachingResource> update(@RequestBody EduTeachingResource resource) {
        EduTeachingResource result = resourceService.updateResource(resource);
        return AjaxResult.success(result);
    }

    /**
     * 删除资源
     */
    @DeleteMapping("/{resourceId}")
    @PreAuthorize("hasAuthority('edu:resource:remove')")
    public AjaxResult<Boolean> remove(@PathVariable Long resourceId) {
        boolean result = resourceService.deleteResource(resourceId);
        return AjaxResult.success(result);
    }

    /**
     * 获取课程资源列表
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('edu:resource:query')")
    public AjaxResult<List<EduTeachingResource>> getCourseResources(@PathVariable Long courseId) {
        List<EduTeachingResource> resources = resourceService.getResourcesByCourseId(courseId);
        return AjaxResult.success(resources);
    }

    /**
     * 下载资源
     */
    @GetMapping("/download/{resourceId}")
    @PreAuthorize("hasAuthority('edu:resource:download')")
    public AjaxResult<Map<String, Object>> download(@PathVariable Long resourceId) {
        // 增加下载次数
        resourceService.incrementDownloadCount(resourceId);
        
        // 获取资源信息
        EduTeachingResource resource = resourceService.getResourceById(resourceId);

        // 返回URL 以及 文件名
        Map<String, Object> map = new HashMap<>();
        map.put("fileUrl", resource.getFileUrl());
        map.put("fileName", resource.getResourceName());
        return AjaxResult.success(map);
    }

    /**
     * 获取当前用户的资源列表
     */
    @GetMapping("/my/list")
    @PreAuthorize("hasAuthority('edu:resource:query')")
    public AjaxResult<IPage<EduTeachingResource>> getMyResources(
            EduTeachingResource resource,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<EduTeachingResource> page = resourceService.getMyResources(resource,pageNum, pageSize);
        return AjaxResult.success(page);
    }
    
    /**
     * 获取当前角色的资源列表
     */
    @GetMapping("/role/list")
    @PreAuthorize("isAuthenticated()")
    public AjaxResult<IPage<EduTeachingResource>> getRoleResources(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<EduTeachingResource> page = resourceService.getRoleResources(pageNum, pageSize);
        return AjaxResult.success(page);
    }

} 