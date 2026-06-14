package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.model.entity.EduSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EduSectionMapper extends BaseMapper<EduSection> {
    
    /**
     * 根据章节ID查询所有小节
     * 
     * @param chapterId 章节ID
     * @return 小节列表
     */
    List<EduSection> selectSectionsByChapterId(@Param("chapterId") Long chapterId);
    
    /**
     * 根据课程ID查询所有小节
     * 
     * @param courseId 课程ID
     * @return 小节列表
     */
    List<EduSection> selectSectionsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 分页查询章节小节
     * 
     * @param page 分页参数
     * @param chapterId 章节ID
     * @return 小节分页列表
     */
    IPage<EduSection> selectSectionPage(Page<EduSection> page, @Param("chapterId") Long chapterId);
} 