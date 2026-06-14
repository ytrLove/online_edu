package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.model.entity.EduTeachingResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EduTeachingResourceMapper extends BaseMapper<EduTeachingResource> {
    /**
     * 分页查询课程资源列表
     * @param page 分页参数
     * @param resource 查询条件
     * @return 资源列表
     */
    IPage<EduTeachingResource> selectResourceList(Page<EduTeachingResource> page, @Param("resource") EduTeachingResource resource);
    
    /**
     * 获取课程资源列表
     * @param courseId 课程ID
     * @return 资源列表
     */
    List<EduTeachingResource> selectResourcesByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 增加资源下载次数
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int incrementDownloadCount(@Param("resourceId") Long resourceId);
    
    /**
     * 逻辑删除资源
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int logicDeleteResource(@Param("resourceId") Long resourceId);
} 