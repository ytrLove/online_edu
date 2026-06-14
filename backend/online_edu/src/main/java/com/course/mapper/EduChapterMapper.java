package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.model.entity.EduChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EduChapterMapper extends BaseMapper<EduChapter> {
    
    /**
     * 根据课程ID查询所有章节
     * 
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<EduChapter> selectChaptersByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 分页查询课程章节
     * 
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 章节分页列表
     */
    IPage<EduChapter> selectChapterPage(Page<EduChapter> page, @Param("courseId") Long courseId);
} 