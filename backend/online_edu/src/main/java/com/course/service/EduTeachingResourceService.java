package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.model.entity.EduTeachingResource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface EduTeachingResourceService extends IService<EduTeachingResource> {
    /**
     * 分页查询课程资源列表
     * @param resource 查询条件
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页数据
     */
    IPage<EduTeachingResource> listResources(EduTeachingResource resource, Integer pageNum, Integer pageSize);
    
    /**
     * 获取资源详情
     * @param resourceId 资源ID
     * @return 资源详情
     */
    EduTeachingResource getResourceById(Long resourceId);
    
    /**
     * 上传资源

     * @param resource 资源信息
     * @return 上传的资源
     */
    Boolean uploadResource(EduTeachingResource resource);
    
    /**
     * 更新资源信息
     * @param resource 资源信息
     * @return 更新后的资源
     */
    EduTeachingResource updateResource(EduTeachingResource resource);
    
    /**
     * 删除资源
     * @param resourceId 资源ID
     * @return 是否成功
     */
    boolean deleteResource(Long resourceId);
    
    /**
     * 获取课程资源列表
     * @param courseId 课程ID
     * @return 资源列表
     */
    List<EduTeachingResource> getResourcesByCourseId(Long courseId);
    
    /**
     * 增加资源下载次数
     * @param resourceId 资源ID
     * @return 是否成功
     */
    boolean incrementDownloadCount(Long resourceId);
    
    /**
     * 获取当前用户的资源列表
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页资源列表
     */
    IPage<EduTeachingResource> getMyResources(EduTeachingResource resource ,Integer pageNum, Integer pageSize);
    
    /**
     * 获取当前角色的资源列表
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页资源列表
     */
    IPage<EduTeachingResource> getRoleResources(Integer pageNum, Integer pageSize);
} 